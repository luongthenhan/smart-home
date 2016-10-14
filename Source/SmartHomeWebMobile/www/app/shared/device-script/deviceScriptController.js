app.directive("deviceScript", ['MainService', function(MainService){
    return {
        restrict: 'E',
        scope: {
            script: '=',
            deviceId: '='
        },
        templateUrl: "app/shared/device-script/deviceScriptView.html",
        controllerAs: "deviceScriptCtrl",
        controller: function($scope) {
            var self = this;

            self.init = function() {
                console.log($scope.script);
            }
        }

    }
}])