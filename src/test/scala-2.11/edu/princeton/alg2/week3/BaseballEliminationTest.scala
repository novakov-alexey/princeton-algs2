package edu.princeton.alg2.week3

import edu.princeton.cs.algs4.StdOut
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.JavaConversions._

/**
  * @author Alexey Novakov
  */
class BaseballEliminationTest extends FlatSpec with Matchers {
  behavior of "BaseballElimination"

  def fixture = {
    new {
      val fileName = "teams4.txt"
      val division = new BaseballElimination(s"baseball/$fileName")
    }
  }

  it should "load data from file" in {
    //when
    val division = fixture.division
    //then
    division.numberOfTeams shouldBe 4
    //then
    division.teams.toSet shouldBe Set("New_York", "Atlanta", "Philadelphia", "Montreal")
    //then
    division.wins("New_York") shouldBe 78
    //then
    division.losses("Montreal") shouldBe 82
    //then
    division.remaining("Philadelphia") shouldBe 3
    //then
    division.against("Atlanta", "Philadelphia") shouldBe 1
  }

  it should "return eliminated" in {
    fixture.division.isEliminated("Montreal") shouldBe true
  }

  it should "return certificate of elimination" in {
    fixture.division.certificateOfElimination("Montreal").toSet shouldBe Set("Atlanta")
  }

  def printEliminatedTeams(fileName: String) {
    val division: BaseballElimination = new BaseballElimination(fileName)

    division.teams.foreach {
      case t if division.isEliminated(t) =>
        StdOut.print(s"$t is eliminated by the subset R = { ")
        division.certificateOfElimination(t).foreach(et => StdOut.print(s"$et "))
        StdOut.println("}")
      case t => StdOut.println(s"$t is not eliminated")
    }
  }
}
