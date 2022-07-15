package io.github.lanicc.zookits.command;

import java.util.Objects;

/**
 * Created on 2022/7/13.
 *
 * @author lan
 */
public class HelloWorldRequest extends Request<String> {

    private String message;

    public String getMessage() {
        return message;
    }

    public HelloWorldRequest setMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HelloWorldRequest that = (HelloWorldRequest) o;
        return Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message);
    }

    @Override
    public String toString() {
        return "HelloWorldRequest{" +
                "message='" + message + '\'' +
                '}';
    }
}
