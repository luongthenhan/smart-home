app.directive("deviceScriptCustom", ['MainService', function(MainService) {
    return {
        restrict: "E",
        scope: {
            script: "=",
            device: "="
        },
        templateUrl: "app/shared/device-script-custom/deviceScriptCustomView.html",
        controllerAs: "deviceScriptCustomCtrl",
        controller: function($scope) {
            var self = this;

            self.scriptName = $scope.script.name;
            self.scriptContent = $scope.script.content;

            self.init = function() {

            }

            self.deleteScript = function() {
                MainService.deleteScript($scope.device, $scope.script);
            }

            self.updateScript = function() {
                $scope.script.name = self.scriptName;
                $scope.script.content = self.scriptContent;
                MainService.updateCustomScript($scope.script);
            }
        }
    }
}])
