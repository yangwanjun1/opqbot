package io.github.yangwanjun1.constants;

public enum OptionCode {
    EXIT_GROUP(4247),
    REMOVE_GROUPER(2208),
    BEN_GROUPER(4691),
    ;
    private final int code;

    OptionCode(int code) {
        this.code = code;
    }
    public int getCode(){
        return this.code;
    }
}
