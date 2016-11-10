app.controller('HomeController', ['MainService', '$routeParams', function (MainService, $routeParams) {
    var self = this;

    self.deviceTypes = [];
    self.modes = [];
    self.selectedMode = null;
    self.activatedMode = MainService.selectedHome.currentMode;

    self.init = function() {
        MainService.devicePanelCtrlList = [];
        MainService.setUpForSelectedMode(self);
    }

    self.updateModeChange = function() {
        MainService.selectedMode = self.selectedMode;
        MainService.setUpForSelectedMode(self);
    }

}])