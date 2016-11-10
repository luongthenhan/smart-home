app.directive("deviceScriptFromTo", ['MainService', function(MainService) {
    return {
        restrict: "E",
        scope: {
            script: '=',
            device: '='
        },
        templateUrl: "app/shared/device-script-from-to/deviceScriptFromToView.html",
        controllerAs: "deviceScriptFromToCtrl",
        controller: function($scope) {
            var self = this;

            self.script = $scope.script;
            self.scriptId = $scope.script.id;

            self.currentDevice = null;
            self.fromTime = null;
            self.toTime = null;

            self.selectedAction = null;

            self.setUpTimePicker = function(){
                $('.clockpicker').clockpicker({
                    autoclose: true
                });
            }

            self.init = function() {
                self.setUpTimePicker();

                MainService.deviceScriptCtrlList.push(self);
                self.initializeData();
            }

            self.initializeData = function() {

                // Get current device
                self.currentDevice = $.grep(MainService.selectedModeConditionableDevices, function(device) {
                    return device.id == $scope.device.id;
                })[0];

                // check whether the device that contains this script is existed on the UI
                if (typeof self.currentDevice == 'undefined') {
                    // if not existed, then remove itself from devicteScriptCtrlList of MainService to prevent updating this controller
                    MainService.deviceScriptCtrlList = $.grep(MainService.deviceScriptCtrlList, function(whenThenCtrl) {
                        return whenThenCtrl != self;
                    })
                } else {
                    // if existed

                    // Parse selected action from action list
                    self.selectedAction = $.grep(self.currentDevice.actions, function (act) {
                        return $scope.script.content.indexOf(act.script) != -1;
                    })[0];
                }

                var scriptInfo = MainService.parseScriptInfo($scope.script);
                self.fromTime = scriptInfo.fromTime;
                self.toTime = scriptInfo.toTime;
            }

            self.deleteScript = function() {
                MainService.deleteScript($scope.script);
            }

            self.updateActionChange = function () {
                var newActionContent = self.selectedAction.script
                    .replace(/ /g, "")
                    .replace("$DID$", "'" + $scope.device.id + "'")
                    .replace("[","")
                    .replace("]","");
                $scope.script.content = $scope.script.content
                    .replace(/ /g, "")
                    .replace(self.scriptInfo.actionContent, newActionContent)
                    .replace(/ /g, "");
                self.scriptInfo = MainService.parseScriptInfo($scope.script);
                MainService.updateScript($scope.device, $scope.script);
            }
        }
    }
}]);

