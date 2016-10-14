app.directive("deviceScriptWhenThen", ['MainService', function(MainService) {
    return {
        restrict: "E",
        scope: {
            script: "=",
            deviceId: "="
        },
        templateUrl: "app/shared/device-script-when-then/deviceScriptWhenThenView.html",
        controllerAs: "deviceScriptWhenThenCtrl",
        controller: function($scope) {
            var self = this;
            self.isDeleted = false;

            self.currentDevice = null;
            self.scriptId = $scope.script.id;
            self.scriptContent = $scope.script.content.replace(" ", "");
            self.scriptCondInfo = self.scriptContent.substring(self.scriptContent.split('[', 3).join('[').length + 1,
                self.scriptContent.indexOf(']')).split(",");

            self.otherDevices = [];
            self.conditions = [];
            self.actions = [];

            self.selectedOtherDevice = null;
            self.selectedCondition = null;
            self.selectedConditionParam = null;
            self.selectedAction = null;

            self.init = function() {

                // Get current device
                self.currentDevice = $.grep(MainService.devices, function(device) {
                    return device.id == $scope.deviceId;
                })[0];

                // Filter keep only devices that not the current device
                self.otherDevices = $.grep(MainService.devices, function(device) {
                    return device.id != $scope.deviceId;
                })

                // Parse selected device from script
                self.selectedOtherDevice = $.grep(self.otherDevices, function(device){
                    return device.id == parseInt(self.scriptCondInfo[0].replace(/'/g, ""));
                })[0];

                // Get conditions from selected other device
                self.conditions = self.selectedOtherDevice.conditions;

                // Get action from current device
                self.actions = self.currentDevice.actions;

                // Parse selected condition and it's param from condition list
                self.selectedCondition = $.grep(self.conditions, function(cond) {
                    var condScript = cond.script;
                    if (cond.hasParameter) {
                        condScript = condScript.substring(0, condScript.lastIndexOf(","));
                        self.selectedConditionParam = parseFloat(self.scriptCondInfo[2].replace(/'/g, ""));
                    }
                    return self.scriptContent.indexOf(condScript) != -1;
                })[0];

                // Parse selected action from action list
                self.selectedAction = $.grep(self.actions, function(act) {
                    return self.scriptContent.indexOf(act.script) != -1;
                })[0];
            }

            self.updateDeviceChange = function() {
                // Get conditions from selected other device
                self.conditions = self.selectedOtherDevice.conditions;
                self.selectedCondition = self.conditions[0];
            }

            self.updateConditionChange = function () {
                var newScriptCondInfo = "'" + self.selectedOtherDevice.id + "','" + self.selectedCondition.name + "','";
                if (self.selectedCondition.hasParameter) {
                    newScriptCondInfo = newScriptCondInfo + self.selectedConditionParam + "'";
                } else {
                    newScriptCondInfo = newScriptCondInfo + "'";
                }
                console.log("new script: " + newScriptCondInfo.replace(/ /g,""));
            }

            self.deleteScript = function() {
                MainService.deleteScript($scope.deviceId, $scope.script.id);
                self.isDeleted = true;
            }
        }
    }
}])
