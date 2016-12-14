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
        var newScript = {};
		newScript.name = self.newCustomScriptName;
        newScript.content = self.newCustomScriptContent;
        newScript.type = {
            id: 3
        }
        if (MainService.addCustomScript(newScript) == true) {
            self.init();
        }
    }

}])
