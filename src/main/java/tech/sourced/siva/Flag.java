package tech.sourced.siva;

enum Flag {
    NO_FLAG, DELETE;

    public static Flag fromInteger(int i) {
        switch (i) {
            case 1:
                return DELETE;
            default:
                return NO_FLAG;
        }
    }
}
