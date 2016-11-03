app.controller('DeviceListController', ['MainService', '$routeParams', function (MainService, $routeParams) {
    var self = this;

    self.deviceType = null;
    self.devices = [];

    self.modes = [];
    self.selectedMode = null;

    //variable for handling add device
    self.selectedModeAvailableGpios = [];
    self.selectedGpio = null;
    self.newDeviceName = null;
    self.newDeviceDescription = null;

    self.init = function() {
        // self.modes = MainService.selectedHome.modes;
        // self.selectedMode = MainService.selectedMode;
        // self.deviceType = MainService.selectedDeviceType;
        // MainService.getDevices(self);
        MainService.deviceListCtrl = self;
        self.initializeData();
    }

    self.initializeData = function() {

        self.selectedModeAvailableGpios = MainService.selectedModeAvailableGpios;
        self.selectedGpio = self.selectedModeAvailableGpios[0];

        self.deviceType = $.grep(MainService.allDeviceTypes, function(devType){
            return devType.id == $routeParams.deviceTypeId;
        })[0];

        self.devices = $.grep(MainService.selectedModeConditionableDevices, function(dev) {
            return dev.deviceType.id == $routeParams.deviceTypeId;
        })
    }

    self.updateModeChange = function() {
        MainService.selectedMode = self.selectedMode;
    }

    self.addDevice = function() {
        var newDevice = {};
        newDevice.gpio = self.selectedGpio;
        newDevice.name = self.newDeviceName;
        newDevice.description = self.newDeviceDescription;
        newDevice.enabled = true;
        MainService.addDevice(newDevice);
    }

}])