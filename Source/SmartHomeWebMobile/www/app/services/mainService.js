app.service('MainService', function($http) {

    var self = this;
	
	self.TOKEN_HEADER_NAME = "X-Auth-Token";
	self.USERNAME_HEADER_NAME = "X-Username";
	self.PASSWORD_HEADER_NAME = "X-Password";

    self.hostDomain = "http://localhost:8080/smarthome/api/";
    self.userId = 1;
    self.selectedHome = null;
    self.selectedMode = null;
    self.selectedDeviceType = null;
    self.devices = [];
	self.authToken = null;

    self.getHomes = function(controller) {
        console.log("URL: " + self.hostDomain + "users/" + self.userId + "/homes");
        $http.get(self.hostDomain + "users/" + self.userId + "/homes", {
					headers: {self.TOKEN_HEADER_NAME : self.authToken}
			}).then(function(response){
            //TODO: Handle home lists

            //STUB: select first home and first mode in home lists and its device types
            self.selectedHome = response.data[0];
            self.selectedMode = self.selectedHome.currentMode;

            controller.modes = self.selectedHome.modes;
            $.each(controller.modes, function(index, val){
                if (val.id == self.selectedMode.id) {
                    controller.selectedMode = val;
                }
            })
            self.getDeviceTypes(controller);
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
                $http.get(self.hostDomain + "homes/" + self.selectedHome.id + "/device-types/" + dtVal.id + "/devices", {
					headers: {self.TOKEN_HEADER_NAME : self.authToken}
			}).then(function(response){
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
        $http.get(self.hostDomain + "homes/" + self.selectedHome.id + "/device-types/" + self.selectedDeviceType.id + "/devices", {
					headers: {self.TOKEN_HEADER_NAME : self.authToken}
			}).then(function(response){
            controller.devices = response.data;
            $.each(controller.devices, function(index, dVal) {
                dVal.modes = [];
                self.getSelectedModeScripts(dVal);
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

    self.getSelectedModeScripts = function(device) {
        $http.get(self.hostDomain + "devices/" + device.id + "/modes/" + self.selectedMode.id + "/scripts", {
					headers: {self.TOKEN_HEADER_NAME : self.authToken}
			}).then(function(response){
            device.scripts = response.data;
            console.log(device.scripts);
        })
    }

    self.getAllScripts = function(device) {
        $.each(self.selectedHome.modes, function(index, val) {
            $http.get(self.hostDomain + "devices/" + device.id + "/modes/" + val.id + "/scripts", {
					headers: {self.TOKEN_HEADER_NAME : self.authToken}
			}).then(function(response){
                device.modes.push({
                    id: val.id,
                    name: val.name,
                    scripts: response.data
                })
            })
        })
    }

    self.updateScript = function(controller) {
        // TODO: Call web services /script/update
        return true;
    }

    self.deleteScript = function(deviceId, scriptId) {
        $http.delete(self.hostDomain + "/" + "devices/" + deviceId + "/modes/" + self.selectedMode.id
            + "/scripts/" + scriptId, {
					headers: {self.TOKEN_HEADER_NAME : self.authToken}
			}).then(function(response) {
                console.log("Delete Script");
                console.log(response.headers);
        })
        return true;
    }

    self.addScript = function (controller) {
        // TODO: Call web services /script/add
        return true;
    }
	
	self.login = function(username, password) {
		$http.get(self.hostDomain + "/" + "login", {
					headers: {self.USERNAME_HEADER_NAME : username, self.PASSWORD_HEADER_NAME : password }
			}).then(function(response) {
				
				if(response.status == 200) {
					self.authToken = response.headers(self.TOKEN_HEADER_NAME);
					return true;
				}
				
				return false;
		})
	}
	
	self.logout = function() {
		$http.get(self.hostDomain + "/" + "logout", {
					headers: {self.TOKEN_HEADER_NAME : self.authToken}
		}).then(function(response) {
				
			if(response.status == 200) {
				return true;
			}
				
			return false;
		})
	}
	
})