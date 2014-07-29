package controllers;

import com.salesforce.canvas.SignedRequest;
import play.Play;
import play.mvc.*;

import views.html.*;

import java.util.HashMap;
import java.util.Map;

public class Application extends Controller {

    public static Result setup() {
        if (getConsumerSecret() == null) {
            Boolean remote = request().hasHeader("X-Forwarded-Proto");
            String url = routes.Application.setup().absoluteURL(request(), true);
            return internalServerError(setup.render(remote, url));
        }
        else {
            return ok(ready.render());
        }
    }

    public static Result index() {
        if (getConsumerSecret() == null) {
            return redirect(routes.Application.setup());
        } else {
            Map<String, String[]> parameters = request().body().asFormUrlEncoded();
            if (parameters == null) {
                parameters = new HashMap<String, String[]>();
            }
            String[] signedRequest = parameters.get("signed_request");
            if (signedRequest == null) {
                return unauthorized("This app must be invoked via a signed request!");
            }
            else {
                String signedRequestJson = SignedRequest.verifyAndDecodeAsJson(signedRequest[0], getConsumerSecret());
                return ok(index.render(signedRequestJson));
            }
        }
    }

    private static String getConsumerSecret() {
        return Play.application().configuration().getString("salesforce.oauth.consumer-secret");
    }

}
