export class SecurityService {

    constructor(duration) {
        this.duration = duration || 60;
        if(!window.Keycloak) {
            throw "keycloak.js library is not available";
        }
        this.keycloak = Keycloak({
            url: 'http://127.0.0.1:9999/auth',
            realm: 'demo',
            clientId: 'public'
        });
        setInterval(() => this.updateToken(), (this.duration - 2) * 1000);
    }

    init() {
        return this.keycloak
            .init({onLoad: 'login-required', promiseType: 'native'})
            .then(() => this.keycloak.loadUserProfile())
            .then(profile => {
                this.profile = profile;
                return Promise.resolve();
            });
    }

    getToken() {
        return this.keycloak.token;
    }

    getUsername() {
        return this.profile.username;
    }

    updateToken() {
        this.keycloak.updateToken(this.duration).then(() => console.log('Authentication token updated'));
    }

}
