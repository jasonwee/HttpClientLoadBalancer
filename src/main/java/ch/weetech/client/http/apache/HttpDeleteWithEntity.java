package ch.weetech.client.http.apache;

import java.net.URI;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

public class HttpDeleteWithEntity extends HttpEntityEnclosingRequestBase {

    public HttpDeleteWithEntity() {
        super();
    }

    public HttpDeleteWithEntity(URI uri) {
        super();
        setURI(uri);
    }

    public HttpDeleteWithEntity(String uri) {
        super();
        setURI(URI.create(uri));
    }

    @Override
    public String getMethod() {
        return HttpDelete.METHOD_NAME;
    }

}
