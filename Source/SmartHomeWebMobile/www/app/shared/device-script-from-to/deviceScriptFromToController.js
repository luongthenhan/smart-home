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

            self.oldFromTime = null;
            self.oldToTime = null;

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

            self.updateFromTimeChange = function() {
                var scriptInfo = MainService.parseScriptInfo($scope.script);
                var newFromTimeContent = "'FromTo','" + self.fromTime + "'";
                var oldScriptContent = $scope.script.content;
                $scope.script.content = $scope.script.content
                    .replace(/ /g, "")
                    .replace("'FromTo','" + scriptInfo.fromTime + "'", newFromTimeContent)
                    .replace(/ /g, "");
                if (MainService.updateScript($scope.script) == false) {
                    self.fromTime = self.oldFromTime;
                    $scope.script.content = oldScriptContent;
                }
            }

            self.updateToTimeChange = function() {
                var scriptInfo = MainService.parseScriptInfo($scope.script);
                var newToTimeContent = self.toTime + "',[[";
                var oldScriptContent = $scope.script.content;
                $scope.script.content = $scope.script.content
                    .replace(/ /g, "")
                    .replace(scriptInfo.toTime + "',[[", newToTimeContent)
                    .replace(/ /g, "");
                console.log("new script content: " + $scope.script.content);
                if (MainService.updateScript($scope.script) == false) {
                    self.toTime = self.oldToTime;
                    $scope.script.content = oldScriptContent;
                }
            }

            self.updateActionChange = function() {
                var scriptInfo = MainService.parseScriptInfo($scope.script);
                var newActionContent = self.selectedAction.script
                    .replace(/ /g, "")
                    .replace("$DID$", "'" + $scope.device.id + "'")
                    .replace("[","")
                    .replace("]","");
                $scope.script.content = $scope.script.content
                    .replace(/ /g, "")
                    .replace(scriptInfo.actionContent, newActionContent)
                    .replace(/ /g, "");
                MainService.updateScript($scope.script);
            }
        }
    }
}]);

