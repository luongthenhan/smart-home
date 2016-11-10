app.directive("homePanel", ['MainService', function(MainService) {
    return {
        restrict: "E",
        scope: {
            home : "="
        },
        templateUrl: "app/shared/home-panel/homePanelView.html",
        controllerAs: 'homePanelCtrl',
        controller: function($scope, $location) {
            var self = this;
            self.toHome = function() {
                MainService.selectedHomeId = $scope.home.id;
                MainService.getHome(self);
                $location.path("/home");
            }
        }
    };
}]);