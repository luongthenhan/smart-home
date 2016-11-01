var app = angular.module('app', ["ngRoute", "ngMessages"]);

app.config(['$routeProvider', function ($routeProvider) {
    $routeProvider
        .when('/login', {
            templateUrl: 'app/components/login/loginView.html'
        })
        .when('/change_default_user', {
            templateUrl: 'app/components/change_default_user/changeDefaultUserView.html'
        })
        .when('/add_device', {
            templateUrl: 'app/components/add_device/addDeviceView.html'
        })
        .when('/home', {
            templateUrl: 'app/components/home/homeView.html'
        })
        .when('/device_list', {
            templateUrl: 'app/components/device_list/deviceListView.html',
        })
        .when('/register', {
            templateUrl: 'app/components/register/register.html',
        })
        .when('/register_success', {
            templateUrl: 'app/components/register_success/register_success.html',
        })
        .otherwise({
            redirectTo: "/login"
        })

}]);
