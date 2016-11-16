app.service('MainService', function($http, $location, blockUI) {

    var self = this;

    self.hostDomain = "https://localhost:8443/smarthome/api/";
    self.token = "";

    // All gpios
    self.allGpios = [];

    // All device types
    self.allDeviceTypes = [];

    self.selectedHome = null;
    self.selectedMode = null;
    self.selectedDeviceType = null;

    // All devices
    self.allDevices = [];
    // Current available gpios of selected mode
    self.selectedModeAvailableGpios = [];
    // Condition-able (displayable) devices
    self.selectedModeConditionableDevices = [];

    // Keep controllers for updating UI
    self.deviceListCtrl = null; // Device List Controller used when in Device List page
    self.devicePanelCtrlList = []; // Device Panel Controller List used when in Device List page
    self.deviceScriptCtrlList = []; // Device Script Controller List used when in Device List page

    self.login = function(username, password, controller) {
        $http.get(self.hostDomain + "login", {
            headers: {
                'X-Username': username,
                'X-Password': password
            }
        }).then(function(response) {
            self.token = response.headers('X-Auth-Token');
            console.log(self.token);
            $http.get(self.hostDomain + "homes/all-gpios", {
                headers: {
                    'X-Auth-Token': self.token
                }
            }).then(function(response) {
                self.allGpios = response.data;
                controller.redirectToHome();
            })
        }, function(data) {
            window.alert("Username or password is incorrect !");
        });
    }

    self.getHome = function() {
        $.ajax({
            url: self.hostDomain + "/homes/" + self.selectedHome.id,
            type: 'GET',
            beforeSend: function (request) {
                request.setRequestHeader("X-Auth-Token", self.token);
            },
            async: false,
            success: function(data, textStatus, xhr) {
                console.log("get home successfully");

                $.each(self.selectedHome.modes, function(index, val){
                    if (val.id == self.selectedHome.currentMode.id) {
                        self.selectedMode = val;
                    }
                })
            },
            error: function(data, textStatus, xhr) {
                console.log("error get home");
            }
        })
    }

    self.getHomes = function(controller) {
        $http.get(self.hostDomain + "/homes", {
            headers: {
                'X-Auth-Token': self.token
            }
        }).then(function(response){
            controller.homes = response.data;
            console.log(controller.homes);
        })
    }

    self.setUpForSelectedMode = function(controller) {

        controller.modes = self.selectedHome.modes;
        controller.selectedMode = self.selectedMode;

        self.allDevices = [];
        self.selectedModeAvailableGpios = self.allGpios;
        self.selectedModeConditionableDevices = [];

        $.ajax({
            url: self.hostDomain + "homes/" + self.selectedHome.id + "/device-types",
            type: 'GET',
            beforeSend: function (request) {
                request.setRequestHeader("X-Auth-Token", self.token);
            },
            async: false,
            success: function(data, textStatus, xhr) {
                self.allDeviceTypes = data;
                self.allDeviceTypes = $.grep(self.allDeviceTypes, function (dt) {
                    return dt.name.indexOf("Hidden Device") == -1;
                })
                controller.deviceTypes = self.allDeviceTypes;

                // Fetch conditions, actions and scripts
                $.each(self.allDeviceTypes, function(dtIndex, dtVal){
                    $.ajax({
                        url: self.hostDomain + "homes/" + self.selectedHome.id + "/device-types/" + dtVal.id + "/devices",
                        type: 'GET',
                        beforeSend: function (request) {
                            request.setRequestHeader("X-Auth-Token", self.token);
                        },
                        async: false,
                        success: function (data, textStatus, xhr) {
                            $.each(data, function(dIndex, dVal) {
                                self.allDevices.push(dVal);
                                dVal.refNum = 0;
                                self.fetchConditionsAndActions(dtVal, dVal);
                                self.getSelectedModeScripts(dVal);
                            })
                        },
                        error: function (data, textStatus, xhr) {
                            console.log("error get device types");
                        }
                    })
                })

                // Set up condition-able device list
                self.selectedModeConditionableDevices = self.allDevices.slice();
                $.each(self.allDevices, function (adIndex, adValue) {
                    var isCDContainsADValue = typeof $.grep(self.selectedModeConditionableDevices, function (condDev) {
                            return condDev.id == adValue.id;
                        })[0] != "undefined";
                    if (isCDContainsADValue && adValue.scripts.length > 0) {
                        self.markDevice(adValue);
                        $.each(adValue.scripts, function (scpIndex, scpValue) {
                            adValue.refNum++;
                            var scriptInfo = self.parseScriptInfo(scpValue);
                            var conditionDevice = $.grep(self.allDevices, function (ad) {
                                return ad.id == scriptInfo.conditionDeviceId;
                            })[0];

                            if (typeof conditionDevice != "undefined") {
                                self.markDevice(conditionDevice);
                                conditionDevice.refNum++;
                            }
                        })
                    }
                })
            },
            error: function(data, textStatus, xhr) {
                console.log("error get devices");
            }
        })
    }

    self.fetchConditionsAndActions = function (dtVal, dVal) {
        dVal.conditions = [];
        dVal.actions = [];

        $.each(dtVal.conditions, function(condIndex, condVal){
            deviceCondition = {
                hasParameter: condVal.name.indexOf("$V$") != -1,
                name: condVal.name.replace("$DNAME$", "").replace("$V$", ""),
                script: condVal.script.replace("$DID$", "'" + dVal.id + "'").replace(/ /g, ""),
                deviceId: dVal.id
            }
            dVal.conditions.push(deviceCondition);
        })

        $.each(dtVal.actions, function(actIndex, actVal){
            deviceAction = {
                name: actVal.name.replace("$DNAME$", ""),
                script: actVal.script.replace("$DID$", "'" + dVal.id + "'").replace(/ /g, ""),
                deviceId: dVal.id
            }
            dVal.actions.push(deviceAction);
        })
    }

    self.getSelectedModeScripts = function(device) {
        $.ajax({
            url: self.hostDomain + "devices/" + device.id + "/modes/" + self.selectedMode.id + "/scripts",
            type: 'GET',
            beforeSend: function (request) {
                request.setRequestHeader("X-Auth-Token", self.token);
            },
            async: false,
            success: function (data, textStatus, xhr) {
                var scripts = data;
                $.each(scripts, function(scpIndex, scpValue) {
                    scpValue.content = scpValue.content.replace(/ /g, "");
                })
                device.scripts = scripts;
            },
            error: function (data, textStatus, xhr) {

            }
        })
    }

    self.parseScriptInfo = function (script) {
        var scriptInfo = {};
        var scriptContent = script.content.replace(/ /g, "");

        if (script.type.id == 1) {
            var scriptConditionContent = scriptContent.substring(scriptContent.split('[', 3).join('[').length + 1,
                scriptContent.indexOf(']'));
            var scriptActionContent = scriptContent.substring(scriptContent.split('[', 5).join('[').length + 1,
                scriptContent.indexOf(']', scriptContent.indexOf(']') + 1));

            var scriptConditionInfo = scriptConditionContent.split(",");
            var scriptActionInfo = scriptActionContent.split(",");

            scriptInfo.conditionDeviceId = parseInt(scriptConditionInfo[0].replace(/'/g, ""));
            scriptInfo.conditionParam = parseFloat(scriptConditionInfo[2].replace(/'/g, ""));
            scriptInfo.conditionContent = scriptConditionContent;

            scriptInfo.actionDeviceId = parseInt(scriptActionInfo[1].replace(/'/g, ""));
            scriptInfo.actionContent = scriptActionContent;
        } else if (script.type.id == 2) {
            var scriptTimeContent = scriptContent.substring(scriptContent.indexOf(',') + 1, scriptContent.split(',', 3).join(',').length);
            var scriptActionContent = scriptContent.substring(scriptContent.split('[', 4).join(',').length + 1, scriptContent.indexOf(']'));

            var scriptTimeInfo = scriptTimeContent.split(",");
            var scriptActionInfo = scriptActionContent.split(",");

            scriptInfo.fromTime = scriptTimeInfo[0].replace(/'/g, "");
            scriptInfo.toTime = scriptTimeInfo[1].replace(/'/g, "");
            scriptInfo.actionDeviceId = parseInt(scriptActionInfo[1].replace(/'/g, ""));
            scriptInfo.actionContent = scriptActionContent;
        }

        return scriptInfo;
    }

    self.activateMode = function(mode) {
        self.selectedHome.currentMode = mode;
        $.ajax({
            url: self.hostDomain + "homes/" + self.selectedHome.id,
            type: 'PATCH',
            data: JSON.stringify(self.selectedHome),
            dataType: 'json',
            contentType: 'application/json; charset=UTF-8',
            beforeSend: function (request) {
                request.setRequestHeader("X-Auth-Token", self.token);
            },
            async: false,
            success: function (data, textStatus, xhr) {
                if (xhr.status == 204) {
                    console.log("Activate mode successfully");
                }
            },
            error: function (data, textStatus, xhr) {

            }
        })
    }

    self.addMode = function(mode) {
        $.ajax({
            url: self.hostDomain + "homes/" + self.selectedHome.id + "/modes",
            type: 'POST',
            data: JSON.stringify(mode),
            dataType: 'json',
            contentType: 'application/json; charset=UTF-8',
            beforeSend: function (request) {
                request.setRequestHeader("X-Auth-Token", self.token);
            },
            async: false,
            success: function (data, textStatus, xhr) {
                if (xhr.status == 201) {
                    console.log("Add mode successfully");
                    mode.id = data.id;
                    self.selectedHome.modes.push(mode);
                }
            },
            error: function (data, textStatus, xhr) {

            }
        })
    }

    self.deleteMode = function(mode) {

        // Delete all scripts of mode
        $.each(self.allDevices, function(devIndex, devValue) {
            $.ajax({
                url: self.hostDomain + "devices/" + devValue.id + "/modes/" + mode.id + "/scripts",
                type: 'GET',
                beforeSend: function (request) {
                    request.setRequestHeader("X-Auth-Token", self.token);
                },
                async: false,
                success: function (data, textStatus, xhr) {
                    $.each(data, function(scpIndex, scpValue) {
                        self.deleteScript(scpValue);
                    })
                },
                error: function (data, textStatus, xhr) {

                }
            })
        })

        // Delete mode
        $.ajax({
            url: self.hostDomain + "homes/" + self.selectedHome.id + "/modes/" + mode.id,
            type: 'DELETE',
            beforeSend: function (request) {
                request.setRequestHeader("X-Auth-Token", self.token);
            },
            async: false,
            success: function (data, textStatus, xhr) {
                if (xhr.status == 204) {
                    console.log("Delete mode successfully");
                    self.selectedHome.modes = $.grep(self.selectedHome.modes, function(homeMode) {
                        return homeMode.id != mode.id;
                    })
                }
            },
            error: function (data, textStatus, xhr) {

            }
        })
    }

    self.disableDevice = function(device) {

        if (device.refNum > 0) {
            if (device.scripts.length > 0) {
                $.each(device.scripts, function(scpIndex, scpValue) {
                    self.disableScript(scpValue);
                })
            }

            $.each(self.selectedModeConditionableDevices, function (devIndex, devValue) {
                if (devValue.id != device.id) {
                    if (devValue.scripts.length > 0) {
                        $.each(devValue.scripts, function (scpIndex, scpValue) {
                            var scriptInfo = self.parseScriptInfo(scpValue);
                            if (scriptInfo.conditionDeviceId == device.id) {
                                self.disableScript(scpValue);
                            }
                        })
                    }
                }
            })
        }

        device.enabled = false;
        $http.patch(self.hostDomain + "homes/" + self.selectedHome.id + "/device-types/" + self.selectedDeviceType.id
            + "/devices/" + device.id, device, {
            headers: {
                'X-Auth-Token': self.token
            }
        }).then(function(response) {
            if (response.status == 204) {

                console.log("disable device successfully");

                // Reload Device List page
                self.deviceListCtrl.initializeData();

                // Reload each Device Panel
                $.each(self.devicePanelCtrlList, function(panelCtrlIndex, panelCtrlVal) {
                    panelCtrlVal.initializeData();
                })

                // Reload each When Then Script
                $.each(self.deviceScriptCtrlList, function(scpCtrlIndex, scpCtrlVal) {
                    scpCtrlVal.initializeData();
                })
            }
        })
        return true;
    }

    self.enableDevice = function(device) {

        if (device.refNum > 0) {
            if (device.scripts.length > 0) {
                $.each(device.scripts, function(scpIndex, scpValue) {
                    self.enableScript(scpValue);
                })
            }

            $.each(self.selectedModeConditionableDevices, function (devIndex, devValue) {
                if (devValue.id != device.id) {
                    if (devValue.scripts.length > 0) {
                        $.each(devValue.scripts, function (scpIndex, scpValue) {
                            var scriptInfo = self.parseScriptInfo(scpValue);
                            if (scriptInfo.conditionDeviceId == device.id) {
                                self.enableScript(scpValue);
                            }
                        })
                    }
                }
            })
        }

        device.enabled = true;
        $http.patch(self.hostDomain + "homes/" + self.selectedHome.id + "/device-types/" + self.selectedDeviceType.id
            + "/devices/" + device.id, device, {
            headers: {
                'X-Auth-Token': self.token
            }
        }).then(function(response) {
            if (response.status == 204) {

                console.log("enable device successfully");

                // Reload Device List page
                self.deviceListCtrl.initializeData();

                // Reload each Device Panel
                $.each(self.devicePanelCtrlList, function(panelCtrlIndex, panelCtrlVal) {
                    panelCtrlVal.initializeData();
                })

                // Reload each When Then Script
                $.each(self.deviceScriptCtrlList, function(scpCtrlIndex, scpCtrlVal) {
                    scpCtrlVal.initializeData();
                })
            }
        })
        return true;
    }

    self.deleteDevice = function(device) {
        if (device.refNum > 0) {
            if (device.scripts.length > 0) {
                $.each(device.scripts, function(scpIndex, scpValue) {
                    self.deleteScript(scpValue);
                })
            }

            $.each(self.selectedModeConditionableDevices, function (devIndex, devValue) {
                if (devValue.id != device.id) {
                    if (devValue.scripts.length > 0) {
                        $.each(devValue.scripts, function (scpIndex, scpValue) {
                            var scriptInfo = self.parseScriptInfo(scpValue);
                            if (scriptInfo.conditionDeviceId == device.id) {
                                self.deleteScript(scpValue);
                            }
                        })
                    }
                }
            })
        }

        $http.delete(self.hostDomain + "homes/" + self.selectedHome.id + "/device-types/" + self.selectedDeviceType.id + "/devices/" + device.id, {
            headers: {
                'X-Auth-Token': self.token
            }
        }).then(function(response) {
            if (response.status == 204) {
                // Remove device from conditionable device list
                self.selectedModeConditionableDevices = $.grep(self.selectedModeConditionableDevices, function(dev) {
                    return dev.id != device.id;
                })

                // Remove device from all devices
                self.allDevices = $.grep(self.allDevices, function(dev) {
                    return dev.id != device.id;
                })

                // Remove device's panel ctrl from devicePanelCtrlList to prevent updating
                self.devicePanelCtrlList = $.grep(self.devicePanelCtrlList, function(pCtrl) {
                    return pCtrl.device.id != device.id;
                })

                // Reload Device List page
                self.deviceListCtrl.initializeData();

                // Reload each Device Panel
                $.each(self.devicePanelCtrlList, function(panelCtrlIndex, panelCtrlVal) {
                    panelCtrlVal.initializeData();
                })

                // Reload each When Then Script
                $.each(self.deviceScriptCtrlList, function(scpCtrlIndex, scpCtrlVal) {
                    scpCtrlVal.initializeData();
                })
            }
        })

        return true;
    }

    self.addDevice = function(device) {
        $http.post(self.hostDomain + "homes/" + self.selectedHome.id + "/device-types/" + self.selectedDeviceType.id + "/devices"
            , device, {
            headers: {
                'X-Auth-Token': self.token
            }
        }).then(function(response){
            if (response.status == 201) {
                console.log("Add device successfully");
                device.id = response.data.id;
                device.refNum = 0;
                device.deviceType = {
                    id: self.selectedDeviceType.id
                }
                device.scripts = [];

                self.fetchConditionsAndActions(self.selectedDeviceType, device);

                self.allDevices.push(device);
                self.selectedModeConditionableDevices.push(device);

                // Reload Device List page
                self.deviceListCtrl.initializeData();

                // Reload each Device Panel
                $.each(self.devicePanelCtrlList, function(panelCtrlIndex, panelCtrlVal) {
                    panelCtrlVal.initializeData();
                })

                // Reload each When Then Script
                $.each(self.deviceScriptCtrlList, function(scpCtrlIndex, scpCtrlVal) {
                    scpCtrlVal.initializeData();
                })
            }
        })
        return true;
    }

    self.getAllScripts = function(device) {
        $.each(self.selectedHome.modes, function(index, val) {
            $http.get(self.hostDomain + "devices/" + device.id + "/modes/" + val.id + "/scripts",{
                headers: {
                    'X-Auth-Token': self.token
                }
            }).then(function(response){
                device.modes.push({
                    id: val.id,
                    name: val.name,
                    scripts: response.data
                })
            })
        })
    }

    self.updateScript = function(script, oldSelectedOtherDevice) {
        var result = false;

        // Parse script info
        scriptInfo = self.parseScriptInfo(script);

        // Get the device that script belong to
        device = $.grep(self.selectedModeConditionableDevices, function(dev) {
            return dev.id == scriptInfo.actionDeviceId;
        })[0];

        $http.patch(self.hostDomain + "/devices/" + device.id + "/modes/" + self.selectedMode.id + "/scripts/" + script.id, script,{
            headers: {
                'X-Auth-Token': self.token
            }
        }).then(function(response){
            if (response.status == 200) {
                console.log("Update script successfully");

                // Mark new device and Unmark old device if script is When/Then
                if (script.type.id == 1 && oldSelectedOtherDevice != null && typeof oldSelectedOtherDevice != "undefined") {

                    oldSelectedOtherDevice.refNum--;
                    if (oldSelectedOtherDevice.refNum == 0) {
                        self.unmarkDevice(oldSelectedOtherDevice);
                    }

                    selectedOtherDevice = $.grep(self.selectedModeConditionableDevices, function (dev) {
                        return dev.id == scriptInfo.conditionDeviceId;
                    })[0];

                    if (selectedOtherDevice.refNum == 0) {
                        self.markDevice(selectedOtherDevice);
                    }
                    selectedOtherDevice.refNum++;

                    // Reload Device List page
                    self.deviceListCtrl.initializeData();

                    // Reload each Device Panel
                    $.each(self.devicePanelCtrlList, function (panelCtrlIndex, panelCtrlVal) {
                        panelCtrlVal.initializeData();
                    })

                    // Reload each When Then Script
                    $.each(self.deviceScriptCtrlList, function (scpCtrlIndex, scpCtrlVal) {
                        scpCtrlVal.initializeData();
                    })
                }
                result = true;
            } else {
                window.alert("Update Failed !\n" + response.data.content);
                result = false;
            }
        }, function(error) {
            window.alert("Update Failed !\n" + error.data.content);
            result = false;
        })

        // $.ajax({
        //     url: self.hostDomain + "/devices/" + device.id + "/modes/" + self.selectedMode.id + "/scripts/" + script.id,
        //     type: 'PATCH',
        //     data: JSON.stringify(script),
        //     dataType: 'json',
        //     contentType: 'application/json; charset=UTF-8',
        //     beforeSend: function (request)
        //     {
        //         request.setRequestHeader("X-Auth-Token", self.token);
        //     },
        //     async: true,
        //     success: function(data, textStatus, xhr) {
        //         if (xhr.status == 200) {
        //             console.log("Update script successfully");
        //
        //             // Mark new device and Unmark old device if script is When/Then
        //             if (script.type.id == 1 && oldSelectedOtherDevice != null && typeof oldSelectedOtherDevice != "undefined") {
        //
        //                 oldSelectedOtherDevice.refNum--;
        //                 if (oldSelectedOtherDevice.refNum == 0) {
        //                     self.unmarkDevice(oldSelectedOtherDevice);
        //                 }
        //
        //                 selectedOtherDevice = $.grep(self.selectedModeConditionableDevices, function (dev) {
        //                     return dev.id == scriptInfo.conditionDeviceId;
        //                 })[0];
        //
        //                 if (selectedOtherDevice.refNum == 0) {
        //                     self.markDevice(selectedOtherDevice);
        //                 }
        //                 selectedOtherDevice.refNum++;
        //
        //                 // Reload Device List page
        //                 self.deviceListCtrl.initializeData();
        //
        //                 // Reload each Device Panel
        //                 $.each(self.devicePanelCtrlList, function(panelCtrlIndex, panelCtrlVal) {
        //                     panelCtrlVal.initializeData();
        //                 })
        //
        //                 // Reload each When Then Script
        //                 $.each(self.deviceScriptCtrlList, function(scpCtrlIndex, scpCtrlVal) {
        //                     scpCtrlVal.initializeData();
        //                 })
        //             }
        //             result = true;
        //         }
        //     },
        //     error: function(data, textStatus, xhr) {
        //         window.alert("Update Failed !\n" + $.parseJSON(data.responseText).content);
        //         result = false;
        //     }
        // })

        return result;
    }

    self.disableScript = function(script) {
        script.enabled = false;

        // Parse script info
        scriptInfo = self.parseScriptInfo(script);

        // Get the device that script belong to
        device = $.grep(self.selectedModeConditionableDevices, function(dev) {
            return dev.id == scriptInfo.actionDeviceId;
        })[0];

        $.ajax({
            url: self.hostDomain + "devices/" + device.id + "/modes/" + self.selectedMode.id + "/scripts/" + script.id,
            type: 'PATCH',
            data: JSON.stringify(script),
            dataType: 'json',
            contentType: 'application/json; charset=UTF-8',
            beforeSend: function (request)
            {
                request.setRequestHeader("X-Auth-Token", self.token);
            },
            async: false,
            success: function(data, textStatus, xhr) {
                if (xhr.status == 204) {
                    console.log("Disable script successfully");

                    // Reload Device List page
                    self.deviceListCtrl.initializeData();

                    // Reload each Device Panel
                    $.each(self.devicePanelCtrlList, function(panelCtrlIndex, panelCtrlVal) {
                        panelCtrlVal.initializeData();
                    })

                    // Reload each When Then Script
                    $.each(self.deviceScriptCtrlList, function(scpCtrlIndex, scpCtrlVal) {
                        scpCtrlVal.initializeData();
                    })
                }
            },
            error: function(data, textStatus, xhr) {
                console.log("error disable script: ");
                console.log(data);
            }
        })
    }

    self.enableScript = function(script) {
        script.enabled = true;

        // Parse script info
        scriptInfo = self.parseScriptInfo(script);

        // Get the device that script belong to
        device = $.grep(self.selectedModeConditionableDevices, function(dev) {
            return dev.id == scriptInfo.actionDeviceId;
        })[0];

        $.ajax({
            url: self.hostDomain + "devices/" + device.id + "/modes/" + self.selectedMode.id + "/scripts/" + script.id,
            type: 'PATCH',
            data: JSON.stringify(script),
            dataType: 'json',
            contentType: 'application/json; charset=UTF-8',
            beforeSend: function (request)
            {
                request.setRequestHeader("X-Auth-Token", self.token);
            },
            async: false,
            success: function(data, textStatus, xhr) {
                if (xhr.status == 204) {
                    console.log("Enable script successfully");

                    // Reload Device List page
                    self.deviceListCtrl.initializeData();

                    // Reload each Device Panel
                    $.each(self.devicePanelCtrlList, function(panelCtrlIndex, panelCtrlVal) {
                        panelCtrlVal.initializeData();
                    })

                    // Reload each When Then Script
                    $.each(self.deviceScriptCtrlList, function(scpCtrlIndex, scpCtrlVal) {
                        scpCtrlVal.initializeData();
                    })
                }
            },
            error: function(data, textStatus, xhr) {
                console.log("error enable script: ");
                console.log(data);
            }
        })
    }

    self.deleteScript = function(script) {
        // Parse script info
        scriptInfo = self.parseScriptInfo(script);

        // Get the device that script belong to
        device = $.grep(self.selectedModeConditionableDevices, function(dev) {
            return dev.id == scriptInfo.actionDeviceId;
        })[0];

        $.ajax({
            url: self.hostDomain + "/devices/" + device.id + "/modes/" + self.selectedMode.id + "/scripts/" + script.id,
            type: 'DELETE',
            beforeSend: function (request)
            {
                request.setRequestHeader("X-Auth-Token", self.token);
            },
            async: false,
            success: function(data, textStatus, xhr) {
                if (xhr.status == 200) {
                    console.log("Delete script successfully");
                    // Remove deleted script from device's scripts
                    device.scripts = $.grep(device.scripts, function (scp) {
                        return scp.id != script.id;
                    })

                    device.refNum--;
                    if (device.refNum == 0) {
                        self.unmarkDevice(device);
                    }

                    if (script.type.id == 1) {
                        selectedOtherDevice = $.grep(self.selectedModeConditionableDevices, function (dev) {
                            return dev.id == scriptInfo.conditionDeviceId;
                        })[0];

                        selectedOtherDevice.refNum--;
                        if (selectedOtherDevice.refNum == 0) {
                            self.unmarkDevice(selectedOtherDevice);
                        }

                        // Remove script's when then ctrl from deviceScriptCtrlList to prevent updating
                        self.deviceScriptCtrlList = $.grep(self.deviceScriptCtrlList, function(wtCtrl) {
                            return wtCtrl.currentDevice.id != device.id;
                        })
                    }

                    // Reload Device List page
                    self.deviceListCtrl.initializeData();

                    // Reload each Device Panel
                    $.each(self.devicePanelCtrlList, function(panelCtrlIndex, panelCtrlVal) {
                        panelCtrlVal.initializeData();
                    })

                    // Reload each When Then Script
                    $.each(self.deviceScriptCtrlList, function(scpCtrlIndex, scpCtrlVal) {
                        scpCtrlVal.initializeData();
                    })
                }
            },
            error: function(data, textStatus, xhr) {
                console.log("error delete script");
            }
        })

    }

    self.addScript = function (script) {
        console.log("Add script: " + script.content);

        // Parse script info
        scriptInfo = self.parseScriptInfo(script);

        // Get the device that script belong to
        device = $.grep(self.selectedModeConditionableDevices, function(dev) {
            return dev.id == scriptInfo.actionDeviceId;
        })[0];

        $http.post(self.hostDomain + "devices/" + device.id + "/modes/" + self.selectedMode.id
            + "/scripts", script,{
            headers: {
                'X-Auth-Token': self.token
            }
        }).then(function(response) {
            if (response.status == 201) {
                console.log("Add script successfully");
                // Set id for new script and add it to device
                script.id = response.data.id;
                device.scripts.push(script);

                if (device.refNum == 0) {
                    self.markDevice(device);
                }
                device.refNum++;

                if (script.type.id == 1) {
                    selectedOtherDevice = $.grep(self.selectedModeConditionableDevices, function (dev) {
                        return dev.id == scriptInfo.conditionDeviceId;
                    })[0];

                    if (selectedOtherDevice.refNum == 0) {
                        self.markDevice(selectedOtherDevice);
                    }
                    selectedOtherDevice.refNum++;
                }

                // Reload Device List page
                self.deviceListCtrl.initializeData();

                // Reload each Device Panel
                $.each(self.devicePanelCtrlList, function(panelCtrlIndex, panelCtrlVal) {
                    panelCtrlVal.initializeData();
                })

                // Reload each When Then Script
                $.each(self.deviceScriptCtrlList, function(scpCtrlIndex, scpCtrlVal) {
                    scpCtrlVal.initializeData();
                })

            } else if (response.status == 400) {
                window.alert("This script is conflicted with other script(s) !");
            }
        })
        return true;
    }

    self.register = function (controller) {
        var user = {"usrName" : controller.username, "password" : controller.password,
            "name" : controller.fullname, "email" : controller.email};

        var ERROR_WHEN_ADD_USER = -1;
        var USERNAME_ALREADY_EXISTS = -2;
        var EMAIL_ALREADY_EXISTS = -3;
        var returnCode = 0;
        $http.post(self.hostDomain + "users/signup", user)
            .success(function (data, status, headers, config) {
                $location.path("/register_success");
            })
            .error(function (data, status, header, config) {
                returnCode = data.returnCode;
                if (returnCode == EMAIL_ALREADY_EXISTS) {
                    controller.emailExists = true;
                }
                else if (returnCode == USERNAME_ALREADY_EXISTS) {
                    controller.usernameExists = true;
                }
            });

    }

    self.markDevice = function (device) {
        self.selectedModeAvailableGpios = $.grep(self.selectedModeAvailableGpios, function(gpio) {
            return gpio != device.gpio;
        })
        self.selectedModeConditionableDevices = $.grep(self.selectedModeConditionableDevices, function(conditionableDev){
            return conditionableDev.id == device.id || conditionableDev.gpio != device.gpio;
        })
    }

    self.unmarkDevice = function (device) {
        $.each(self.allDevices, function(devId, devVal) {
            if (devVal.id != device.id && devVal.gpio == device.gpio) {
                self.selectedModeConditionableDevices.push(devVal);
            }
        })
        self.selectedModeAvailableGpios.push(device.gpio);
        self.selectedModeAvailableGpios = $.unique(self.selectedModeAvailableGpios);
    }

    self.addHome = function (home) {
        $.ajax({
            url: self.hostDomain + "homes",
            type: 'POST',
            data: JSON.stringify(home),
            dataType: 'json',
            contentType: 'application/json; charset=UTF-8',
            beforeSend: function (request) {
                request.setRequestHeader("X-Auth-Token", self.token);
            },
            async: false,
            success: function (data, textStatus, xhr) {
                if (xhr.status == 201) {
                    console.log("Add home successfully");
                    home.id = data.id;
                }
            },
            error: function (data, textStatus, xhr) {

            }
        })
    }

    self.deleteHome = function (home) {

        self.selectedHome = home;
        self.getHome({});

        $.each(self.selectedHome.modes, function(modeIndex, modeValue) {
            self.selectedMode = modeValue;
            self.setUpForSelectedMode({});
            self.deleteMode(modeValue);
        })

        $.each(self.allDevices, function(devIndex, devValue) {
            self.deleteDevice(devValue);
        })

        $.ajax({
            url: self.hostDomain + "homes/" + home.id,
            type: 'DELETE',
            beforeSend: function (request) {
                request.setRequestHeader("X-Auth-Token", self.token);
            },
            async: false,
            success: function (data, textStatus, xhr) {
                if (xhr.status == 204) {
                    console.log("Delete home successfully");
                }
            },
            error: function (data, textStatus, xhr) {

            }
        })
    }

    self.disableHome = function(home) {
        home.enabled = false;
        $.ajax({
            url: self.hostDomain + "homes/" + home.id,
            type: 'PATCH',
            data: JSON.stringify(home),
            dataType: 'json',
            contentType: 'application/json; charset=UTF-8',
            beforeSend: function (request) {
                request.setRequestHeader("X-Auth-Token", self.token);
            },
            async: false,
            success: function (data, textStatus, xhr) {
                if (xhr.status == 204) {
                    console.log("Disable home successfully");
                }
            },
            error: function (data, textStatus, xhr) {

            }
        })
    }

    self.enableHome = function(home) {
        home.enabled = true;
        $.ajax({
            url: self.hostDomain + "homes/" + home.id,
            type: 'PATCH',
            data: JSON.stringify(home),
            dataType: 'json',
            contentType: 'application/json; charset=UTF-8',
            beforeSend: function (request) {
                request.setRequestHeader("X-Auth-Token", self.token);
            },
            async: false,
            success: function (data, textStatus, xhr) {
                if (xhr.status == 204) {
                    console.log("Enable home successfully");
                }
            },
            error: function (data, textStatus, xhr) {

            }
        })
    }
})