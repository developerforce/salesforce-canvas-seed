import play.Play
import play.api.http.HeaderNames
import play.api.mvc._
import play.filters.gzip.GzipFilter
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

object Global extends WithFilters(new GzipFilter(), OnlyHttpsFilter)

object OnlyHttpsFilter extends Filter {
  def apply(nextFilter: (RequestHeader) => Future[Result])(requestHeader: RequestHeader): Future[Result] = {
    nextFilter(requestHeader).map { result =>
      if (requestHeader.secure) {
        result
      }
      else {
        // not secure
        requestHeader.headers.get(HeaderNames.X_FORWARDED_PROTO).fold {
          // running locally
          sys.props.get("https.port").fold {
            // no https support
            val msg = "HTTPS is required.  Run Play with HTTPS enabled via the command line: activator -Dhttps.port=9443 ~run"
            Results.InternalServerError(msg)
          } { httpsPort =>
            Results.MovedPermanently("https://" + requestHeader.domain + ":" + httpsPort + requestHeader.uri)
          }
        } { proto =>
          // running behind a proxy
          Results.MovedPermanently("https://" + requestHeader.domain + requestHeader.uri)
        }
      }
    }
  }
}