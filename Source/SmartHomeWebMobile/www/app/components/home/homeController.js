app.controller('HomeController', ['MainService', '$routeParams', '$route', function (MainService, $routeParams, $route) {
    var self = this;

    self.deviceTypes = [];
    self.modes = [];
    self.selectedMode = null;
    self.activatedMode = MainService.selectedHome.currentMode;

    //Info for add new mode
    self.newModeName = null;

    self.init = function() {
        // Refresh devicePanelCtrlList, deviceWhenThenCtrlList in MainService to clear old controllers from updating
        MainService.devicePanelCtrlList = [];
        MainService.deviceScriptCtrlList = [];

        MainService.setUpForSelectedMode(self);
    }

    self.updateModeChange = function() {
        MainService.selectedMode = self.selectedMode;
        MainService.setUpForSelectedMode(self);
    }

    self.addNewMode = function() {
        var newMode = {};
        newMode.name = self.newModeName;
        MainService.addMode(newMode);
        $route.reload();
    }

}])