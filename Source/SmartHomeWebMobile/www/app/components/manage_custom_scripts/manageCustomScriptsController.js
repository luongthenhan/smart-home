app.controller('ManageCustomScriptsController', ['MainService', '$routeParams', '$route', function (MainService, $routeParams, $route) {
    var self = this;

    // Info for add new custom script
    self.newCustomScriptName = "";
    self.newCustomScriptContent = "";

    self.device = null;

    self.init = function() {
        self.device = MainService.hiddenDevice;
    }

    self.addNewCustomScript = function() {

    }

}])
