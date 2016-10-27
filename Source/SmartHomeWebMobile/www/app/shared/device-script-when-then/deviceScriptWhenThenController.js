app.directive("deviceScriptWhenThen", ['MainService', function(MainService) {
    return {
        restrict: "E",
        scope: {
            script: "=",
            device: "="
        },
        templateUrl: "app/shared/device-script-when-then/deviceScriptWhenThenView.html",
        controllerAs: "deviceScriptWhenThenCtrl",
        controller: function($scope) {
            var self = this;

            self.currentDevice = null;
            self.scriptId = $scope.script.id;
            self.scriptContent = $scope.script.content.replace(/ /g, "");
            self.scriptCondInfo = self.scriptContent.substring(self.scriptContent.split('[', 3).join('[').length + 1,
                self.scriptContent.indexOf(']')).split(",");
            self.scriptActionContent = self.scriptContent.substring(self.scriptContent.split('[', 5).join('[').length + 1,
                self.scriptContent.indexOf(']', self.scriptContent.indexOf(']') + 1));

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
                    return device.id == $scope.device.id;
                })[0];

                // Filter keep only devices that not the current device
                self.otherDevices = $.grep(MainService.devices, function(device) {
                    return device.id != $scope.device.id && device.gpio != $scope.device.gpio;
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

                var newScriptContent = "";
                newScriptContent = "[['If'," + self.selectedCondition.script
                        .replace(/ /g, "")
                        .replace("$DID$", "'" + self.selectedOtherDevice.id + "'") + ",[";
                newScriptContent = newScriptContent + self.selectedAction.script
                        .replace(/ /g, "")
                        .replace("$DID$", "'" + $scope.device.id + "'") + "]]]";
                if (self.selectedCondition.hasParameter) {
                    newScriptContent = newScriptContent.replace("$V$", "'" + self.selectedConditionParam + "'");
                }
                $scope.script.content = newScriptContent;
                self.parseInfoFromScript();
                MainService.updateScript($scope.device.id, $scope.script);
            }

            self.updateConditionChange = function () {
                var newCondInfo = self.selectedCondition.script
                    .replace(/ /g, "")
                    .replace("$DID$", "'" + self.selectedOtherDevice.id + "'")
                    .replace("[","")
                    .replace("]","");
                if (self.selectedCondition.hasParameter) {
                    newCondInfo = newCondInfo.replace("$V$", "'" + self.selectedConditionParam + "'");
                }
                $scope.script.content = $scope.script.content
                    .replace(/ /g, "")
                    .replace(self.scriptCondInfo, newCondInfo)
                    .replace(/ /g, "");
                self.parseInfoFromScript();
                MainService.updateScript($scope.device.id, $scope.script);

            }

            self.updateConditionParamChange = function () {
                var newCondInfo = self.scriptCondInfo.slice();
                console.log(newCondInfo);
                newCondInfo[2] = "'" + self.selectedConditionParam + "'";
                $scope.script.content = $scope.script.content
                    .replace(/ /g, "")
                    .replace(self.scriptCondInfo, newCondInfo)
                    .replace(/ /g, "");
                self.parseInfoFromScript();
                MainService.updateScript($scope.device.id, $scope.script);
            }

            self.updateActionChange = function () {
                var newActionContent = self.selectedAction.script
                    .replace(/ /g, "")
                    .replace("$DID$", "'" + $scope.device.id + "'")
                    .replace("[","")
                    .replace("]","");
                $scope.script.content = $scope.script.content
                    .replace(/ /g, "")
                    .replace(self.scriptActionContent, newActionContent)
                    .replace(/ /g, "");
                self.parseInfoFromScript();
                MainService.updateScript($scope.device, $scope.script);
            }

            self.deleteScript = function() {
                MainService.deleteScript($scope.device, $scope.script.id, self.selectedOtherDevice);
            }

            self.parseInfoFromScript = function() {
                self.scriptContent = $scope.script.content.replace(/ /g, "");
                self.scriptCondInfo = self.scriptContent.substring(self.scriptContent.split('[', 3).join('[').length + 1,
                    self.scriptContent.indexOf(']')).split(",");
                self.scriptActionContent = self.scriptContent.substring(self.scriptContent.split('[', 5).join('[').length + 1,
                    self.scriptContent.indexOf(']', self.scriptContent.indexOf(']') + 1));
            }
        }
    }
}])
