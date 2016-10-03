<<<<<<< HEAD
app.service('MainService', function($http) {

    this.hostDomain = "http://localhost:8080/smarthome/api/";
    this.homeId = 1;

    this.selectedDeviceType = null;
    this.selectedMode = null;

    this.getModes = function(controller) {
        // TODO: Call web services /mode?homeId=0
        controller.modes = [
            {
                "id": 1,
                "name": "Default"
            },
            {
                "id": 2,
                "name": "Vacation"
            }
        ]
        controller.selectedMode = {
            "id": 1,
            "name": "Default"
        }
    }

    this.getDeviceTypes = function(controller) {
        //$http.get(this.hostDomain + "devices/type").then(function(response){
        //    controller.deviceTypes = response.data;
        //})
        // TODO: Call web services /device/categories?homeId=0
        controller.deviceTypes = [
            {
                "id": 1,
                "name": "Light",
                "imageURL": "assets/img/light-bulb.png",
                "mainAction": {
                    "id": 1,
                    "name": "Toggle",
                    "actionScript": "['Toggle', D]"
                },
                "conditions": [
                    {
                        "id": 1,
                        "name": "When D off",
                        "conditionScript": "[D,'=',false]"
                    },
                    {
                        "id": 2,
                        "name": "When D on",
                        "conditionScript": "[D,'=',true]"
                    }
                ],
                "actions": [
                    {
                        "id": 1,
                        "name": "Toggle",
                        "actionScript": "['Toggle', D]"
                    },
                    {
                        "id": 2,
                        "name": "Turn on",
                        "actionScript": "['Turn on', D]"
                    },
                    {
                        "id": 3,
                        "name": "Turn off",
                        "actionScript": "['Turn off', D]"
                    }
                ]
            }
        ]
    }

    this.getDevices = function(controller) {
        //$http.get(this.hostDomain + "homes/" + this.homeId + "/devices/type/" + this.selectedDeviceType.id).then(function(response){
        //    controller.devices = response.data;
        //})
        // TODO: Call web services /device/list?homeId=0&categoryId=|n|
        controller.devices = [
            {
                "id": 1,
                "name": "Light Bulb 1",
                "description": "light at 1st floor",
                "location": "1st floor",
                "GPIO": 1,
                "status": "On",
                "scripts": [
                    {
                        "id": 1,
                        "content": "[['If',[1,'=',false],[['Toggle',4]]]]",
                        "type": {
                            "id": 1,
                            "name": "If/Then"
                        },
                        "name": null
                    },
                    {
                        "id": 2,
                        "content": "[['If',[1,'=',true],[['Toggle',4]]]]",
                        "type": {
                            "id": 3,
                            "name": "Custom"
                        },
                        "name": "Custom script 1"
                    }
                ],
                "enabled": true
            }
        ]
    }

    this.enableDevice = function(controller) {
        // TODO: Call web services /device/enable?deviceId=|n|
        return true;
    }

    this.disableDevice = function(controller) {
        // TODO: Call web services /device/disable?deviceId=|n|
        return true;
    }

    this.deleteDevice = function(controller) {
        // TODO: Call web services /device/delete?deviceId=|n|
        return true;
    }

    this.addDevice = function(controller) {
        // TODO: Call web services /device/add
        return true;
    }

    this.getScripts = function(controller) {
        // TODO: Call web services /script/list?deviceId=|n|&modeId=|n|
        controller.scripts = [
            {
                "id": 1,
                "content": "[['If',[1,'=',false],[['Toggle',4]]]]",
                "type": {
                    "id": 1,
                    "name": "If/Then"
                },
                "name": null
            }
        ]
    }

    this.updateScript = function(controller) {
        // TODO: Call web services /script/update
        return true;
    }

    this.deleteScript = function(controller) {
        // TODO: Call web services /script/delete?scriptId=|n|
        return true;
    }

    this.addScript = function (controller) {
        // TODO: Call web services /script/add
        return true;
    }

=======
app.service('MainService', function($http) {

    this.hostDomain = "http://192.168.1.103:8080/smarthome/api/";
    this.homeId = 1;

    this.selectedDeviceType = null;

    this.getDeviceTypes = function(controller) {
        $http.get(this.hostDomain + "devices/type").then(function(response){
            controller.deviceTypes = response.data;
        })
    }

    this.getDevices = function(controller) {
        $http.get(this.hostDomain + "homes/" + this.homeId + "/devices/type/" + this.selectedDeviceType.id).then(function(response){
            controller.devices = response.data;
        })
    }

>>>>>>> 0704e7edb4a1e704f859268bfa4c87efd7acf1d6
})