app.controller('DeviceListController', ['MainService', '$routeParams', function (MainService, $routeParams) {
    var self = this;

    self.deviceType = null;
    self.devices = [];

    self.modes = [];
    self.selectedMode = null;

    self.init = function() {
        self.modes = MainService.selectedHome.modes;
        self.selectedMode = MainService.selectedMode;
        self.deviceType = MainService.selectedDeviceType;
        MainService.getDevices(self);
    }

    self.updateModeChange = function() {
        MainService.selectedMode = self.selectedMode;
    }

}])