import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._


@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification {

  "Application" should {

    "not allow unknown users in" in new WithApplication{
      val home = route(FakeRequest(GET, "/helloUser?username=unknown@tunnelbear.com&password=password")).get

      status(home) must equalTo(FORBIDDEN)
      contentAsString(home) must contain ("Invalid username or password")
    }

    "not allow known users with incorrect passwords in" in new WithApplication{
      val home = route(FakeRequest(GET, "/helloUser?username=bob@tunnelbear.com&password=password_bad")).get

      status(home) must equalTo(FORBIDDEN)
      contentAsString(home) must contain ("Invalid username or password")
    }

    "not allow non-premium users in" in new WithApplication{
      val home = route(FakeRequest(GET, "/helloUser?username=jane@tunnelbear.com&password=password")).get

      status(home) must equalTo(FORBIDDEN)
      contentAsString(home) must contain ("Conditions not met: User must be premium")
    }

    "not allow premium users with low balance in" in new WithApplication{
      val home = route(FakeRequest(GET, "/helloUser?username=alice@tunnelbear.com&password=password")).get

      status(home) must equalTo(FORBIDDEN)
      contentAsString(home) must contain ("Conditions not met: User balance must be >")
    }

    "allow known users with correct passwords in" in new WithApplication{
      val home = route(FakeRequest(GET, "/helloUser?username=bob@tunnelbear.com&password=password")).get

      status(home) must equalTo(OK)
      contentAsString(home) must contain ("hello bob@tunnelbear.com")
    }

  }
}
