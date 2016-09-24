app.controller('DeviceListController', ['MainService', function (MainService) {
    var self = this;

    self.deviceType = null;
    self.devices = [];

    self.init = function() {
        self.deviceType = MainService.selectedDeviceType;
        MainService.getDevices(self);
    }

}])