app.controller('HomeController', ['MainService', '$routeParams', function (MainService, $routeParams) {
    var self = this;

    self.deviceTypes = [];
    self.modes = [];
    self.selectedMode = null;

    self.init = function() {
        MainService.devicePanelCtrlList = [];
        MainService.getHome(self);
    }

    self.updateModeChange = function() {
        MainService.selectedMode = self.selectedMode;
    }

}])