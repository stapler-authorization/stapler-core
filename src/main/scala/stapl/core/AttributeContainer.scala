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
package stapl.core

import scala.language.dynamics
import scala.collection.mutable.Map
import scala.collection.mutable.Buffer


class AttributeContainer private(cType: AttributeContainerType, attributes: Map[String, Attribute]) extends Dynamic {

  final def this(cType: AttributeContainerType) = this(cType, Map("id" -> SimpleAttribute(cType, "id", String)))
  
  // import the type alias for uninitialized attributes
  import AttributeConstruction.UninitializedAttribute
  
  final def selectDynamic(name: String): Attribute =
    try attributes(name)
    catch {
      case _: NoSuchElementException => throw new AttributeDoesNotExistException(name)
    }
  
  final def updateDynamic(name: String)(attribute: UninitializedAttribute){
    val (optionName, attributeConstructor) = attribute
    val actualAttribute = optionName match {
      case Some(someName) => attributeConstructor(cType, someName)
      case None => attributeConstructor(cType, name)
    }
    attributes += name -> actualAttribute
    refinements.foreach(_.updateDynamic(name)(attribute))
  }
  
  private val refinements = Buffer.empty[AttributeContainer]
  
  final def refine(): AttributeContainer = {
    val refinement = new AttributeContainer(cType, attributes.clone)
    refinements += refinement
    refinement
  }
}


sealed abstract class AttributeContainerType
case object SUBJECT extends AttributeContainerType
case object RESOURCE extends AttributeContainerType
case object ENVIRONMENT extends AttributeContainerType
case object ACTION extends AttributeContainerType