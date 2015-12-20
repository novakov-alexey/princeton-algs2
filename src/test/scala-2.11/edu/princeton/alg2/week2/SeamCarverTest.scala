package edu.princeton.alg2.week2

import java.awt.Color

import edu.princeton.cs.algs4.Picture
import org.scalatest.{FlatSpec, Matchers}

/**
  * @author Alexey Novakov
  */
class SeamCarverTest extends FlatSpec with Matchers {
  behavior of "SeamCarver"

  def fixture = {
    new {
      val picture = new Picture(3, 4)
      picture.set(0, 0, new Color(255, 101, 51))
      picture.set(1, 0, new Color(255, 101, 153))
      picture.set(2, 0, new Color(255, 101, 255))

      picture.set(0, 1, new Color(255, 153, 51))
      picture.set(1, 1, new Color(255, 153, 153))
      picture.set(2, 1, new Color(255, 153, 255))

      picture.set(0, 2, new Color(255, 203, 51))
      picture.set(1, 2, new Color(255, 204, 153))
      picture.set(2, 2, new Color(255, 205, 255))

      picture.set(0, 3, new Color(255, 255, 51))
      picture.set(1, 3, new Color(255, 255, 153))
      picture.set(2, 3, new Color(255, 255, 255))

      val seamCarver = new SeamCarver(picture)
    }
  }

  it should "return current picture" in {
    //given
    val picture = fixture.picture
    //when
    val seamCarver = new SeamCarver(picture)
    //then
    seamCarver.picture should be(picture)
    seamCarver.width should be(3)
    seamCarver.height should be(4)
  }

  it should "return energy" in {
    //given
    val seamCarver = fixture.seamCarver
    //when-then
    seamCarver.energy(0, 0) should be(1000)
    seamCarver.energy(0, 1) should be(1000)
    seamCarver.energy(0, 2) should be(1000)
    seamCarver.energy(0, 3) should be(1000)

    seamCarver.energy(1, 0) should be(1000)
    seamCarver.energy(1, 1) should be(Math.sqrt(52225))
    seamCarver.energy(1, 2) should be(Math.sqrt(52024))
    seamCarver.energy(1, 3) should be(1000)

    seamCarver.energy(2, 0) should be(1000)
    seamCarver.energy(2, 1) should be(1000)
    seamCarver.energy(2, 2) should be(1000)
    seamCarver.energy(2, 3) should be(1000)
  }

  it should "throw index out of bounds exception" in {
    intercept[IndexOutOfBoundsException] {
      fixture.seamCarver.energy(0, -1)
    }
  }

  it should "find vertical seam" in {
    //when
    val verticalSeam = fixture.seamCarver.findVerticalSeam
    //then
    verticalSeam should be(Array(0, 1, 1, 0))
  }

  it should "find horizontal seam" in {
    //when
    val horizontalSeam = fixture.seamCarver.findHorizontalSeam
    //then
    horizontalSeam should be(Array(1, 2, 1))
  }

  it should "remove vertical seam" in {
    //given
    val seamCarver = fixture.seamCarver
    //when
    seamCarver.removeVerticalSeam(seamCarver.findVerticalSeam)
    //then
    val picture = seamCarver.picture
    picture.width should be(2)
    picture.height should be(4)

    //then
    val scaledPicture = new Picture(2, 4)
    scaledPicture.set(0, 0, new Color(255, 101, 153))
    scaledPicture.set(1, 0, new Color(255, 101, 255))

    scaledPicture.set(0, 1, new Color(255, 153, 51))
    scaledPicture.set(1, 1, new Color(255, 153, 255))

    scaledPicture.set(0, 2, new Color(255, 203, 51))
    scaledPicture.set(1, 2, new Color(255, 205, 255))

    scaledPicture.set(0, 3, new Color(255, 255, 153))
    scaledPicture.set(1, 3, new Color(255, 255, 255))

    picture should be(scaledPicture)
  }
}