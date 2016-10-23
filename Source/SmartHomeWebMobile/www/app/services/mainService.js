app.service('MainService', function($http) {

    var self = this;

    self.hostDomain = "http://localhost:8080/smarthome/api/";
    self.userId = 2;

    self.selectedHome = null;
    self.selectedMode = null;
    self.selectedDeviceType = null;

    // Current available gpios of selected mode
    self.selectedModeAvailableGpios = [];

    // All devices of selected home
    self.devices = [];

    // All devices of selected type
    self.selectedTypeDevices = [];

    // Current device list controller
    self.deviceListCtrl = null;

    self.getHomes = function(controller) {
        console.log("URL: " + self.hostDomain + "users/" + self.userId + "/homes");
        $http.get(self.hostDomain + "users/" + self.userId + "/homes").then(function(response){
            //TODO: Handle home lists

            //STUB: select first home and first mode in home lists and its device types
            self.selectedHome = response.data[1];
            self.selectedMode = self.selectedHome.currentMode;

            controller.modes = self.selectedHome.modes;
            $.each(controller.modes, function(index, val){
                if (val.id == self.selectedMode.id) {
                    controller.selectedMode = val;
                }
            })

            $http.get(self.hostDomain + "homes/all-gpios").then(function(response) {
                self.selectedModeAvailableGpios = response.data;
                self.getDeviceTypes(controller);
            })

        })
    }

    self.getDeviceTypes = function(controller) {
        self.devices = [];
        self.conditions = [];
        self.actions = [];
        $http.get(self.hostDomain + "homes/" + self.selectedHome.id + "/device-types").then(function(response){
            controller.deviceTypes = response.data;

            // Fetch conditions and actions
            $.each(controller.deviceTypes, function(dtIndex, dtVal){
                $http.get(self.hostDomain + "homes/" + self.selectedHome.id + "/device-types/" + dtVal.id + "/devices").then(function(response){
                    $.each(response.data, function(dIndex, dVal) {
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

                        self.devices.push(dVal);
                    })
                })
            })
        })
    }

    self.getDevices = function(controller) {
        self.deviceListCtrl = controller;
        $http.get(self.hostDomain + "homes/" + self.selectedHome.id + "/device-types/" + self.selectedDeviceType.id + "/devices").then(function(response){
            self.selectedTypeDevices = response.data;
            controller.devices = [];
            $.each(self.selectedTypeDevices, function(index, dVal) {
                dVal.modes = [];

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
        $http.get(self.hostDomain + "devices/" + device.id + "/modes/" + self.selectedMode.id + "/scripts").then(function(response){

            device.scripts = response.data;

            // check if device gpio is available or device is not gpio-need type or device is in-type (sensor)
            if (device.gpio == null || device.gpio == 0 || device.gpiotype == 'in' ||
                $.inArray(device.gpio, self.selectedModeAvailableGpios) != -1) {

                // check whether there are any scripts of this device that belong to the selectedMode or device is in-type
                if (device.scripts.length != 0 || device.gpiotype == 'in') {
                    //if it does have, remove its gpio from available gpios
                    self.selectedModeAvailableGpios = $.grep(self.selectedModeAvailableGpios, function (gpioId) {
                        return gpioId != device.gpio;
                    })

                    // and filter: remove any devices that have same gpio with this device
                    controller.devices = $.grep(controller.devices, function(dev) {
                        return dev.id == device.id || dev.gpio != device.gpio;
                    })
                }
                controller.devices.push(device);
            }
        })
    }

    self.getAllScripts = function(device) {
        $.each(self.selectedHome.modes, function(index, val) {
            $http.get(self.hostDomain + "devices/" + device.id + "/modes/" + val.id + "/scripts").then(function(response){
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

    self.deleteScript = function(device, scriptId) {
        $http.delete(self.hostDomain + "/devices/" + device.id + "/modes/" + self.selectedMode.id
            + "/scripts/" + scriptId).then(function(response) {

            if (response.status == 204) {
                // Remove deleted script from device's scripts
                device.scripts = $.grep(device.scripts, function (scp) {
                    return scp.id != scriptId;
                })

                // After script removed, check if device doesn't have any scripts left
                if (device.scripts.length == 0) {

                    // Then re-available that device gpio
                    self.selectedModeAvailableGpios.push(device.gpio);

                    // and re-available other devices with same gpio with this device
                    $.grep(self.selectedTypeDevices, function(dev) {
                        if (dev.id != device.id && dev.gpio == device.gpio) {
                            self.deviceListCtrl.devices.push(dev);
                        }
                    })
                }
            }
        })
        return true;
    }

    self.addScript = function (device, script) {
        var isDoesNotHaveAnyScriptBefore = (device.scripts.length == 0);
        $http.post(self.hostDomain + "/devices/" + device.id + "/modes/" + self.selectedMode.id
            + "/scripts", script).then(function(response) {
            if (response.status == 201) {
                script.id = response.data.id;
                device.scripts.push(script);
                if (isDoesNotHaveAnyScriptBefore) {

                    //if it does not have any script before adding, remove its gpio from available gpios
                    self.selectedModeAvailableGpios = $.grep(self.selectedModeAvailableGpios, function (gpioId) {
                        return gpioId != device.gpio;
                    })

                    // and filter: remove any devices that have same gpio with this device
                    self.deviceListCtrl.devices = $.grep(self.deviceListCtrl.devices, function (dev) {
                        return dev.id == device.id || dev.gpio != device.gpio;
                    })
                }
            }
        })
        return true;
    }
})