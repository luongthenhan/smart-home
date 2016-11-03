app.service('MainService', function($http, $location) {

    var self = this;

    self.hostDomain = "https://localhost:8443/smarthome/api/";
    self.token = "";

    // All gpios
    self.allGpios = [];

    // All device types
    self.allDeviceTypes = [];

    // Nav bar controller
    self.navBarCtrl = null;

    self.modes = [];
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
    self.deviceWhenThenCtrlList = []; // Device When Then Controller List used when in Device List page

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

    self.getHomes = function(controller) {
        $http.get(self.hostDomain + "/homes", {
            headers: {
                'X-Auth-Token': self.token
            }
        }).then(function(response){
            //TODO: Handle home lists

            //STUB: select first home and first mode in home lists and its device types
            self.selectedHome = response.data[1];

            self.modes = self.selectedHome.modes;
            controller.modes = self.modes;
            self.navBarCtrl.modes = self.modes;

            $.each(controller.modes, function(index, val){
                if (val.id == self.selectedHome.currentMode.id) {
                    controller.selectedMode = val;
                    self.selectedMode = controller.selectedMode;
                    self.navBarCtrl.selectedMode = controller.selectedMode;
                }
            })

            self.setUpForSelectedMode(controller);
        })
    }

    self.setUpForSelectedMode = function(controller) {
        self.allDevices = [];
        self.selectedModeAvailableGpios = self.allGpios;
        self.selectedModeConditionableDevices = [];

        $http.get(self.hostDomain + "homes/" + self.selectedHome.id + "/device-types", {
            headers: {
                'X-Auth-Token': self.token
            }
        }).then(function(response){
            self.allDeviceTypes = response.data;
            controller.deviceTypes = self.allDeviceTypes;

            // Fetch conditions, actions and scripts
            $.each(self.allDeviceTypes, function(dtIndex, dtVal){
                $http.get(self.hostDomain + "homes/" + self.selectedHome.id + "/device-types/" + dtVal.id + "/devices", {
                    headers: {
                        'X-Auth-Token': self.token
                    }
                }).then(function(response){
                    $.each(response.data, function(dIndex, dVal) {
                        self.allDevices.push(dVal);
                        dVal.refNum = 0;
                        self.fetchConditionsAndActions(dtVal, dVal);
                        self.getSelectedModeScripts(dVal);
                    })
                })
            })

            // Set up condition-able device list
            setTimeout(function() {
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

            }, 500);
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
        $http.get(self.hostDomain + "devices/" + device.id + "/modes/" + self.selectedMode.id + "/scripts",{
            headers: {
                'X-Auth-Token': self.token
            }
        }).then(function(response){
            var scripts = response.data;
            $.each(scripts, function(scpIndex, scpValue) {
                scpValue.content = scpValue.content.replace(/ /g, "");
            })
            device.scripts = scripts;
        })
    }

    self.parseScriptInfo = function (script) {
        var scriptInfo = {};

        if (script.type.id == 1) {
            var scriptContent = script.content.replace(/ /g, "");
            var scriptConditionContent = scriptContent.substring(scriptContent.split('[', 3).join('[').length + 1,
                scriptContent.indexOf(']'));
            var scriptActionContent = scriptContent.substring(scriptContent.split('[', 5).join('[').length + 1,
                scriptContent.indexOf(']', scriptContent.indexOf(']') + 1));

            var scriptConditionInfo = scriptConditionContent.split(",");
            scriptInfo.conditionDeviceId = parseInt(scriptConditionInfo[0].replace(/'/g, ""));
            scriptInfo.conditionParam = parseFloat(scriptConditionInfo[2].replace(/'/g, ""));
            scriptInfo.actionContent = scriptActionContent;
        }

        return scriptInfo;
    }

    // self.getDevices = function(controller) {
    //     self.deviceListCtrl = controller;
    //
    //     $http.get(self.hostDomain + "homes/" + self.selectedHome.id + "/device-types/" + self.selectedDeviceType.id + "/devices",{
    //         headers: {
    //             'X-Auth-Token': self.token
    //         }
    //     }).then(function(response){
    //         self.selectedTypeDevices = response.data;
    //
    //         controller.devices = [];
    //
    //         $.each(self.selectedTypeDevices, function(index, dVal) {
    //             dVal.modes = [];
    //             self.fetchConditionsAndActions(self.selectedDeviceType, dVal);
    //             // Get device's scripts that belongs to selected mode and add it to device
    //             self.getSelectedModeScripts(dVal, controller);
    //
    //             // Get all device's scripts and add it device's modes
    //             self.getAllScripts(dVal);
    //         })
    //     })
    // }

    self.enableDevice = function(controller) {
        // TODO: Call web services /device/enable?deviceId=|n|
        return true;
    }

    self.disableDevice = function(controller) {
        // TODO: Call web services /device/disable?deviceId=|n|
        return true;
    }

    self.deleteDevice = function(controller) {
        // TODO: Call web services /device/delete?deviceId=|n|
        return true;
    }

    self.addDevice = function(device) {
        console.log(self.selectedDeviceType);
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
                $.each(self.deviceWhenThenCtrlList, function(whenThenCtrlIndex, whenThenCtrlVal) {
                    whenThenCtrlVal.initializeData();
                })
            }
        })
        return true;
    }

    self.filterConditionReference = function() {
        $.each(self.devices, function(deviceIndex,device)
        {
            $http.get(self.hostDomain + "devices/" + device.id + "/modes/" + self.selectedMode.id + "/scripts", {
                headers: {
                    'X-Auth-Token': self.token
                }
            }).then(function (response) {
                $.each(response.data, function (scriptIndex, scriptVal) {
                    if (scriptVal.type.id == 1) {
                        var scriptContent = scriptVal.content.replace(/ /g, "");
                        var scriptCondInfo = scriptContent.substring(scriptContent.split('[', 3).join('[').length + 1,
                            scriptContent.indexOf(']')).split(",");
                        console.log("script device id: " + scriptCondInfo[0].replace(/'/g, "") + " - " + scriptContent);
                        var selectedOtherDevice = $.grep(self.devices, function (dev) {
                            return dev.id == parseInt(scriptCondInfo[0].replace(/'/g, ""));
                        })[0];
                        if (selectedOtherDevice != null) {
                            console.log("filter out: " + device.id + " - " + selectedOtherDevice.id);

                            $.each(self.devices, function (dIndex, dVal) {
                                if (dVal.id == selectedOtherDevice.id) {
                                    dVal.condRefNum++;
                                    // check if selected other device does not have any condition reference before
                                    if (dVal.condRefNum == 1) {

                                        //remove selected other device gpio from available gpios
                                        self.selectedModeAvailableGpios = $.grep(self.selectedModeAvailableGpios, function (gpioId) {
                                            return gpioId != selectedOtherDevice.gpio;
                                        })

                                        // filter: remove any devices have same gpio with selected other device in the displayed devices
                                        self.deviceListCtrl.devices = $.grep(self.deviceListCtrl.devices, function (dev) {
                                            return dev.id == selectedOtherDevice.id || dev.gpio != selectedOtherDevice.gpio;
                                        })
                                        // filter: remove any devices have same gpio with selected other device in conditions of other devices
                                        $.each(self.devicePanelCtrlList, function (panelCtrlIndex, panelCtrlVal) {
                                            panelCtrlVal.otherDevices = $.grep(panelCtrlVal.otherDevices, function (dev) {
                                                return dev.id == selectedOtherDevice.id || dev.gpio != selectedOtherDevice.gpio;
                                            })
                                        })
                                    }
                                }
                            })
                        }

                    }
                })
            })
        })

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

    self.updateScript = function(device, script) {
        $http.put(self.hostDomain + "/devices/" + device.id + "/modes/" + self.selectedMode.id
            + "/scripts/" + script.id, script, {
            headers: {
                'X-Auth-Token': self.token
            }
        }).then(function(response){
            if (response.status == 204) {
                console.log("Update script successfully");
            }
        })
        return true;
    }

    self.deleteScript = function(device, script, selectedOtherDevice, whenThenCtrl) {
        $http.delete(self.hostDomain + "/devices/" + device.id + "/modes/" + self.selectedMode.id
            + "/scripts/" + script.id,{
            headers: {
                'X-Auth-Token': self.token
            }
        }).then(function(response) {

            if (response.status == 204) {
                console.log("Delete script successfully");
                // Remove deleted script from device's scripts
                device.scripts = $.grep(device.scripts, function (scp) {
                    return scp.id != script.id;
                })

                device.refNum--;
                if (device.refNum == 0) {
                    self.unmarkDevice(device);
                }

                if (script.type.id == 1 && selectedOtherDevice != null) {
                    selectedOtherDevice.refNum--;
                    if (selectedOtherDevice.refNum == 0) {
                        self.unmarkDevice(selectedOtherDevice);
                    }
                    self.deviceWhenThenCtrlList = $.grep(self.deviceWhenThenCtrlList, function(wtCtrl) {
                        return wtCtrl != whenThenCtrl;
                    })
                }

                // Reload Device List page
                self.deviceListCtrl.initializeData();

                // Reload each Device Panel
                $.each(self.devicePanelCtrlList, function(panelCtrlIndex, panelCtrlVal) {
                    panelCtrlVal.initializeData();
                })

                // Reload each When Then Script
                $.each(self.deviceWhenThenCtrlList, function(whenThenCtrlIndex, whenThenCtrlVal) {
                    whenThenCtrlVal.initializeData();
                })

            }
        })
        return true;
    }

    self.addScript = function (device, script, selectedOtherDevice) {
        console.log("Add script: " + script.content);
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

                if (script.type.id == 1 && selectedOtherDevice != null) {
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
                $.each(self.deviceWhenThenCtrlList, function(whenThenCtrlIndex, whenThenCtrlVal) {
                    whenThenCtrlVal.initializeData();
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
})