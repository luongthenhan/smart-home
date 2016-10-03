app.controller('DeviceListController', ['MainService', function (MainService) {
    var self = this;

    self.deviceType = null;
    self.devices = [];

    self.modes = [];
    self.selectedMode = null;

    self.init = function() {
        self.deviceType = MainService.selectedDeviceType;
        MainService.getDevices(self);
        MainService.getModes(self);
    }

}])