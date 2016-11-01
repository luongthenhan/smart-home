app.service('MainService', function($http, $location) {

    var self = this;

    self.hostDomain = "https://localhost:8443/smarthome/api/";
    self.token = "";

    self.userId = 2;

    self.modes = [];
    self.selectedHome = null;
    self.selectedMode = null;
    self.selectedDeviceType = null;

    // Nav bar controller
    self.navBarCtrl = null;

    // All gpios
    self.allGpios = [];

    // Current available gpios of selected mode
    self.selectedModeAvailableGpios = [];

    // All devices of selected mode
    self.devices = [];

    // All devices of selected type
    self.selectedTypeDevices = [];

    // Current device list controller
    self.deviceListCtrl = null;
    // Current device panel controller list
    self.devicePanelCtrlList = [];

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
                console.log(response.status);
                self.allGpios = response.data;
                controller.redirectToHome();
            })
        }, function(data) {
            window.alert("Username or password is incorrect !");
        });
    }

    self.getHomes = function(controller) {
        console.log("URL: " + self.hostDomain + "/homes");
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

            self.selectedModeAvailableGpios = self.allGpios;
            self.getDeviceTypes(controller);

        })
    }

    self.fetchConditionsAndActions = function (dtVal, dVal) {
        dVal.conditions = [];
        dVal.actions = [];

        $.each(dtVal.conditions, function(condIndex, condVal){
            deviceCondition = {
                hasParameter: condVal.name.indexOf("$V$") != -1,
                name: condVal.name.replace("$DNAME$", "").replace("$V$", ""),
                script: condVal.script.replace("$DID$", "'" + dVal.id + "'"),
                deviceId: dVal.id
            }
            dVal.conditions.push(deviceCondition);
        })

        $.each(dtVal.actions, function(actIndex, actVal){
            deviceAction = {
                name: actVal.name.replace("$DNAME$", ""),
                script: actVal.script.replace("$DID$", "'" + dVal.id + "'"),
                deviceId: dVal.id
            }
            dVal.actions.push(deviceAction);
        })
    }

    self.getDeviceTypes = function(controller) {
        self.devices = [];
        self.conditions = [];
        self.actions = [];
        $http.get(self.hostDomain + "homes/" + self.selectedHome.id + "/device-types", {
            headers: {
                'X-Auth-Token': self.token
            }
        }).then(function(response){
            controller.deviceTypes = response.data;

            // Fetch conditions and actions
            $.each(controller.deviceTypes, function(dtIndex, dtVal){
                $http.get(self.hostDomain + "homes/" + self.selectedHome.id + "/device-types/" + dtVal.id + "/devices", {
                    headers: {
                        'X-Auth-Token': self.token
                    }
                }).then(function(response){
                    $.each(response.data, function(dIndex, dVal) {
                        dVal.condRefNum = 0;
                        self.fetchConditionsAndActions(dtVal, dVal);
                        self.getSelectedModeScripts(dVal, self);
                    })
                })
            })
        })
    }

    self.getDevices = function(controller) {
        self.deviceListCtrl = controller;

        $http.get(self.hostDomain + "homes/" + self.selectedHome.id + "/device-types/" + self.selectedDeviceType.id + "/devices",{
            headers: {
                'X-Auth-Token': self.token
            }
        }).then(function(response){
            self.selectedTypeDevices = response.data;

            controller.devices = [];

            $.each(self.selectedTypeDevices, function(index, dVal) {
                dVal.modes = [];
                self.fetchConditionsAndActions(self.selectedDeviceType, dVal);
                // Get device's scripts that belongs to selected mode and add it to device
                self.getSelectedModeScripts(dVal, controller);

                // Get all device's scripts and add it device's modes
                self.getAllScripts(dVal);
            })
        })
    }

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

    self.addDevice = function(controller) {
        // TODO: Call web services /device/add
        return true;
    }

    self.getSelectedModeScripts = function(device, controller) {
        $http.get(self.hostDomain + "devices/" + device.id + "/modes/" + self.selectedMode.id + "/scripts",{
            headers: {
                'X-Auth-Token': self.token
            }
        }).then(function(response){

            device.scripts = response.data;
            // check if device gpio is available or device is not gpio-need type or device is in-type (sensor)
            if (device.gpio == null || device.gpio == 0 || device.gpiotype == 'in' || device.scripts.length != 0 ||
                $.inArray(device.gpio, self.selectedModeAvailableGpios) != -1) {

                // check whether there are any scripts of this device that belong to the selectedMode or device is in-type
                if (device.scripts.length != 0 || device.gpiotype == 'in') {
                    //if it does have, remove its gpio from available gpios
                    self.selectedModeAvailableGpios = $.grep(self.selectedModeAvailableGpios, function (gpioId) {
                        return gpioId != device.gpio;
                    })

                    // and filter: remove any devices that have same gpio with this device
                    controller.devices = $.grep(controller.devices, function (dev) {
                        return dev.id == device.id || dev.gpio != device.gpio;
                    })
                }

                controller.devices.push(device);
            }
        })
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
            + "/scripts/" + script.id, script).then(function(response){

        })
        return true;
    }

    self.deleteScript = function(device, scriptId, selectedOtherDevice) {
        $http.delete(self.hostDomain + "/devices/" + device.id + "/modes/" + self.selectedMode.id
            + "/scripts/" + scriptId,{
            headers: {
                'X-Auth-Token': self.token
            }
        }).then(function(response) {

            if (response.status == 204) {
                // Remove deleted script from device's scripts
                device.scripts = $.grep(device.scripts, function (scp) {
                    return scp.id != scriptId;
                })

                // Minus 1 for condition reference of selected other device
                if (selectedOtherDevice != null) {
                    $.each(self.devices, function(dIndex, dVal) {
                        if (dVal.id == selectedOtherDevice.id) {
                            dVal.condRefNum--;
                            // check if selected other device does not have any condition reference
                            if (dVal.condRefNum == 0) {

                                // Then re-available that device gpio
                                self.selectedModeAvailableGpios.push(selectedOtherDevice.gpio);

                                // re-available for when/then other devices with same gpio with selected other device in the displayed devices
                                $.grep(self.selectedTypeDevices, function (dev) {
                                    if (dev.id != selectedOtherDevice.id && dev.gpio == selectedOtherDevice.gpio) {
                                        self.deviceListCtrl.devices.push(dev);
                                    }
                                })

                                // re-available for when/then other devices with same gpio with this selected other device in conditions of other devices
                                $.each(self.devicePanelCtrlList, function(panelCtrlIndex, panelCtrlVal) {
                                    $.grep(self.selectedTypeDevices, function (dev) {
                                        if (dev.id != selectedOtherDevice.id && panelCtrlVal.device.gpio != selectedOtherDevice.gpio
                                            && dev.gpio == selectedOtherDevice.gpio) {
                                            panelCtrlVal.otherDevices.push(dev);
                                        }
                                    })
                                })
                            }
                        }
                    })

                }

                // After script removed, check if device doesn't have any scripts left
                if (device.scripts.length == 0) {

                    // Then re-available that device gpio
                    self.selectedModeAvailableGpios.push(device.gpio);

                    // and re-available other devices with same gpio with this device in the displayed devices
                    $.grep(self.selectedTypeDevices, function(dev) {
                        if (dev.id != device.id && dev.gpio == device.gpio) {
                            self.deviceListCtrl.devices.push(dev);
                        }
                    })

                    // and re-available other devices with same gpio with this device in conditions of other devices
                    $.each(self.devicePanelCtrlList, function(panelCtrlIndex, panelCtrlVal) {
                        $.grep(self.selectedTypeDevices, function(dev) {
                            if (dev.id != device.id && dev.gpio == device.gpio) {
                                panelCtrlVal.otherDevices.push(dev);
                            }
                        })
                    })

                }
            }
        })
        return true;
    }

    self.addScript = function (device, script, selectedOtherDevice) {
        console.log(script);
        var isDoesNotHaveAnyScriptBefore = (device.scripts.length == 0);
        $http.post(self.hostDomain + "devices/" + device.id + "/modes/" + self.selectedMode.id
            + "/scripts", script,{
            headers: {
                'X-Auth-Token': self.token
            }
        }).then(function(response) {
            if (response.status == 201) {
                script.id = response.data.id;
                device.scripts.push(script);

                // filter for when/then: remove any devices that have same gpio with selected other device in the displayed devices
                if (selectedOtherDevice != null) {

                    // Count 1 for condition reference of selected other device
                    $.each(self.devices, function(dIndex, dVal) {
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

                if (isDoesNotHaveAnyScriptBefore) {
                    //if it does not have any script before adding, remove its gpio from available gpios
                    self.selectedModeAvailableGpios = $.grep(self.selectedModeAvailableGpios, function (gpioId) {
                        return gpioId != device.gpio;
                    })

                    // and filter: remove any devices that have same gpio with this device in the displayed devices
                    self.deviceListCtrl.devices = $.grep(self.deviceListCtrl.devices, function (dev) {
                        return dev.id == device.id || dev.gpio != device.gpio;
                    })

                    // and filter: remove any devices that have same gpio with this device in conditions of other devices
                    $.each(self.devicePanelCtrlList, function(panelCtrlIndex, panelCtrlVal) {
                        panelCtrlVal.otherDevices = $.grep(panelCtrlVal.otherDevices, function(dev){
                            return dev.id == device.id || dev.gpio != device.gpio;
                        })
                    })

                }
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
})