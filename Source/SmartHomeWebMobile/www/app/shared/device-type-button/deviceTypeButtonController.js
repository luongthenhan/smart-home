app.directive("deviceTypeButton", ['MainService', function(MainService) {
    return {
        restrict: "E",
        scope: {
            devicetype: "="
        },
        templateUrl: "app/shared/device-type-button/deviceTypeButtonView.html",
        controllerAs: 'deviceTypeCtrl',
        controller: function($scope, $location) {
            var self = this;
            self.toDeviceList = function() {
                MainService.selectedDeviceType = $scope.devicetype;

                // Before go to Device List page,
                // refresh devicePanelCtrlList, deviceWhenThenCtrlList in MainService to clear old controllers from updating
                MainService.devicePanelCtrlList = [];
                MainService.deviceWhenThenCtrlList = [];

                $location.path("/device_list/" + $scope.devicetype.id);
            }
        }
    }
}])
