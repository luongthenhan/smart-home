app.directive("devicePanel", ['MainService', function(MainService) {
    return {
        restrict: "E",
        scope: {
            device: "=",
            typeimg: "@"
        },
        templateUrl: "app/shared/device-panel/devicePanelView.html",
        controllerAs: "devicePanelCtrl",
        controller: function($scope) {
            var self = this;
            self.isShowDetails = false;

            self.selectedScriptTypeToAdd = "When/Then";
            self.selectedConditionParam = null;
            self.customScriptNameForAdd = null;
            self.customScriptContentForAdd = null;

            self.toggleShowDetails = function() {
                self.isShowDetails = !self.isShowDetails;
            }

            self.init = function() {
                $(".panel-collapse").on('hide.bs.collapse', function () {
                    var deviceId = $(this).attr("id").slice(-1);
                    $("#devicePanelToggleIcon" + deviceId).removeClass("glyphicon-menu-down");
                    $("#devicePanelToggleIcon" + deviceId).addClass("glyphicon-menu-right");

                });
                $(".panel-collapse").on('show.bs.collapse', function () {
                    var deviceId = $(this).attr("id").slice(-1);
                    $("#devicePanelToggleIcon" + deviceId).removeClass("glyphicon-menu-right");
                    $("#devicePanelToggleIcon" + deviceId).addClass("glyphicon-menu-down");

                });

                // Get current device
                self.currentDevice = $.grep(MainService.devices, function(device) {
                    return device.id == $scope.device.id;
                })[0];

                // Filter keep only devices that not the current device
                self.otherDevices = $.grep(MainService.devices, function(device) {
                    return device.id != $scope.device.id && device.gpio != $scope.device.gpio;
                })

                // Parse selected device from script
                self.selectedOtherDevice = self.otherDevices[0];

                // Get conditions from selected other device
                self.conditions = self.selectedOtherDevice.conditions;

                // Get action from current device
                self.actions = self.currentDevice.actions;

                // Parse selected condition and it's param from condition list
                self.selectedCondition = self.conditions[0];

                // Parse selected action from action list
                self.selectedAction = self.actions[0];
            }

            self.updateDeviceChange = function() {
                // Get conditions from selected other device
                self.conditions = self.selectedOtherDevice.conditions;
                self.selectedCondition = self.conditions[0];
            }

            self.addScript = function() {
                var newScript = {};
                var newScriptContent = "";
                if (self.selectedScriptTypeToAdd == "When/Then") {
                    newScriptContent = "[['If'," + self.selectedCondition.script
                            .replace(/ /g, "")
                            .replace("$DID$", "'" + self.selectedOtherDevice.id + "'") + ",[";
                    newScriptContent = newScriptContent + self.selectedAction.script
                            .replace(/ /g, "")
                            .replace("$DID$", "'" + $scope.device.id + "'") + "]]]";
                    if (self.selectedCondition.hasParameter) {
                        newScriptContent = newScriptContent.replace("$V$", "'" + self.selectedConditionParam + "'");
                    }
                    newScript.name = "Name";
                    newScript.content = newScriptContent;
                    newScript.type = {
                        id: 1,
                        name: "IfThen",
                        template: "['If',C, A]"
                    }
                    MainService.addScript($scope.device, newScript);
                } else if (self.selectedScriptTypeToAdd == "Custom") {
                    newScript.name = self.customScriptNameForAdd;
                    newScript.content = self.customScriptContentForAdd;
                    newScript.type = {
                        id: 3,
                        name: "Custom",
                        template: ""
                    }
                    MainService.addScript($scope.device, newScript);
                }

            }

        }
    }
}])


