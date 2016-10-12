app.directive("deviceScriptCustom", ['MainService', function(MainService) {
    return {
        restrict: "E",
        scope: {
            script: "=",
            deviceId: "="
        },
        templateUrl: "app/shared/device-script-custom/deviceScriptCustomView.html",
        controllerAs: "deviceScriptCtrl",
        controller: function($scope) {
            var self = this;

            self.init = function() {

            }
        }
    }
}])
