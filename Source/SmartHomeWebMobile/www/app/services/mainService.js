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

})