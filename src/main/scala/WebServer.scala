import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.pattern.{ask, pipe}
import akka.util.Timeout

import scala.io.StdIn
import scala.util.{Random, Success}
import spray.json._

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.DurationInt

object Models {
  case class Entity(product_id: Int, price: Double)
  final case class GetProducts(number: Int)
  final case class Products(products: Seq[Entity])
}

object MyJsonProtocol extends DefaultJsonProtocol {
  import Models._
  implicit val colorFormat = jsonFormat2(Entity)
}

class MyActor extends Actor {
  import Models._
  implicit val ec = ExecutionContext.global
  implicit val timeout = Timeout(3.seconds)

  val r1 = new Random()

  val v1 = Products(Range(1, 100000).map(x => Entity(r1.nextInt(10000), r1.between(0.0, 1000.0))).toList)//.sortWith((x, y) => x.product_id < y.product_id)
  val v2 = Products(Range(1, 100000).map(x => Entity(r1.nextInt(10000), r1.between(0.0, 1000.0))).toList)//.sortWith((x, y) => x.product_id < y.product_id)
  val v3 = Products(Range(1, 100000).map(x => Entity(r1.nextInt(10000), r1.between(0.0, 1000.0))).toList)//.sortWith((x, y) => x.product_id < y.product_id)

  def receive = {
    case GetProducts(i) => {
      i match {
        case 1 => Future {
          Thread.sleep(1000)
          v1
        }.pipeTo(sender)
        case 2 => Future {
          Thread.sleep(2000)
          v2
        }.pipeTo(sender)
        case 3 => Future {
          Thread.sleep(3000)
          v3
        }.pipeTo(sender)
        case 4 => Future {
          Thread.sleep(6000)
          v3
        }.pipeTo(sender)
      }
    }
  }
}

object WebServer {
  import Models._
  import MyJsonProtocol._

  def main(args: Array[String]): Unit = {

    implicit val actorSystem = ActorSystem("my-system")
    implicit val timeout  = Timeout(30.seconds)
    val service = actorSystem.actorOf(Props(new MyActor()), "my-actor")
    val rand = new Random()

    val route1 =
      path("api1") {
        get {
          onComplete(service ? GetProducts(rand.between(1, 5))) {
            case Success(value: Products) => complete(HttpEntity(ContentTypes.`application/json`, value.products.toJson.toString()))
          }
        }
      }
    val route2 =
      path("api2") {
        get {
          onComplete(service ? GetProducts(rand.between(1, 5))) {
            case Success(value: Products) => complete(HttpEntity(ContentTypes.`application/json`, value.products.toJson.toString()))
          }
        }
      }
    val route3 =
      path("api3") {
        get {
          onComplete(service ? GetProducts(rand.between(1, 5))) {
            case Success(value: Products) => complete(HttpEntity(ContentTypes.`application/json`, value.products.toJson.toString()))
          }
        }
      }
    val route4 =
      path("api4") {
        get {
          onComplete(service ? GetProducts(rand.between(1, 5))) {
            case Success(value: Products) => complete(HttpEntity(ContentTypes.`application/json`, value.products.toJson.toString()))
          }
        }
      }

    Http().bindAndHandle(route1 ~ route2 ~ route3 ~ route4, "0.0.0.0", 8090)
  }
}
