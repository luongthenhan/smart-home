app.directive("deviceScriptFromTo", function() {
    return {
        restrict: "E",
        scope: {},
        templateUrl: "app/shared/device-script-from-to/deviceScriptFromToView.html",
        controllerAs: "deviceScriptFromToCtrl",
        controller: function() {
            var self = this;
            self.setUpTimePicker = function(){
                $('.clockpicker').clockpicker({
                    autoclose: true
                });
            }
        }
    }
});

