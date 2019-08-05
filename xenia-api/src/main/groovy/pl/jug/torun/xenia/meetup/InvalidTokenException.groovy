package pl.jug.torun.xenia.meetup

class InvalidTokenException extends Exception {

    String clientId

    InvalidTokenException(String clientId) {
        super("Token is invalid!")
        this.clientId = clientId
    }

}
