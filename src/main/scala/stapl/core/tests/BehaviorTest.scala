/**
 *    Copyright 2014 KU Leuven Research and Developement - iMinds - Distrinet
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 *    Administrative Contact: dnet-project-office@cs.kuleuven.be
 *    Technical Contact: maarten.decat@cs.kuleuven.be
 *    Author: maarten.decat@cs.kuleuven.be
 */
package stapl.core.tests

import stapl.core.examples.EhealthPolicy
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import stapl.core.pdp.PDP
import stapl.core.pdp.AttributeFinder
import stapl.core.pdp.RequestCtx
import org.junit.Assert._
import stapl.core._
import org.scalatest.junit.AssertionsForJUnit

object BehaviorTest {
  
  @BeforeClass def setup() {
    // nothing to do
  }
}
class BehaviorTest extends AssertionsForJUnit {

  import EhealthPolicy._
  // set up the PDP, use an empty attribute finder since we will provide all attributes in the request
  val pdp = new PDP(naturalPolicy, new AttributeFinder)

  @Before def setup() {
    // nothing to do
  }
  
  @Test def testUndefinedAttribute() {
    val subject = stapl.core.subject // FIXME do we work on the single subject object here? we need a local copy of some sort
    val resource = stapl.core.resource
    val action = stapl.core.action
    val env = stapl.core.environment

    subject.roles = ListAttribute(String)

    intercept[AttributeDoesNotExistException] {
    	Policy("p") := permit iff (subject.nonexistingAttribute === "a-value")
    }
  }

  @Test def testWrongTypeGiven1() {
    intercept[StringIndexOutOfBoundsException] {
      pdp.evaluate("maarten", "view", "doc123",
        subject.roles -> List(1, 2, 3), // FIXME This should give a type exception, no?
        subject.triggered_breaking_glass -> false,
        resource.type_ -> "patientstatus",
        resource.owner_withdrawn_consents -> List("subject1","subject2","subject3","maarten")) 
    } 
  }

  @Test def testWrongTypeGiven2() {
    intercept[StringIndexOutOfBoundsException] {
	  pdp.evaluate("maarten", "view", "doc123",
        subject.roles -> List(1, 2, 3), 
        subject.triggered_breaking_glass -> "blabla", // FIXME This should give a type exception, no?
        resource.type_ -> "patientstatus",
        resource.owner_withdrawn_consents -> List("subject1","subject2","subject3","maarten")) 
    }
  }

  @Test def testWrongTypeGiven3() {
    intercept[StringIndexOutOfBoundsException] {
	  pdp.evaluate("maarten", "view", "doc123",
        subject.roles -> "role", // FIXME This should give a type exception, no?
        subject.triggered_breaking_glass -> false, 
        resource.type_ -> "patientstatus",
        resource.owner_withdrawn_consents -> List("subject1","subject2","subject3","maarten")) 
    }
  }

}