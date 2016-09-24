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
                $location.path("/device_list");
            }
        }
    }
}])
