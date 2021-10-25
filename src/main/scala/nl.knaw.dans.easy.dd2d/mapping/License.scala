/*
 * Copyright (C) 2020 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.knaw.dans.easy.dd2d.mapping

import java.net.URI
import scala.util.Try
import scala.xml.Node

object License {

  private val variantToNormalized = Map(
    "http://www.gnu.org/licenses/gpl-3.0.en.html" -> "http://www.gnu.org/licenses/gpl-3.0",
    "http://www.gnu.org/licenses/lgpl-3.0.txt" -> "http://www.gnu.org/licenses/lgpl-3.0",
    "http://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html" -> "http://www.gnu.org/licenses/old-licenses/gpl-2.0",
    "http://www.mozilla.org/en-US/MPL/2.0/FAQ/" -> "https://mozilla.org/MPL/2.0",
    "http://www.ohwr.org/attachments/735/CERNOHLv1_1.txt" -> "https://ohwr.org/project/cernohl/wikis/Documents/CERN-OHL-version-1.1",
    "http://www.ohwr.org/attachments/2388/cern_ohl_v_1_2.txt" -> "https://ohwr.org/project/cernohl/wikis/Documents/CERN-OHL-version-1.2",
    "http://dans.knaw.nl/en/about/organisation-and-policy/legal-information/DANSLicence.pdf" -> "https://dans.knaw.nl/en/about/organisation-and-policy/legal-information/DANSLicence.pdf")

  def isLicenseUri(node: Node): Boolean = {
    if (node.label != "license") false
    else if (node.namespace != DCTERMS_NAMESPACE_URI) false
         else if (!hasXsiType(node, "URI")) false
              else isValidUri(node.text)
  }

  private def isValidUri(s: String): Boolean = {
    Try {
      new URI(s)
    }.isSuccess
  }

  def getLicenseUri(node: Node): URI = {
    if (isLicenseUri(node)) normalizeLicense(node.text).getOrElse(throw new IllegalArgumentException(s"Not a support license: ${node.text}"))
    else throw new IllegalArgumentException("Not a valid license node")
  }

  def normalizeLicense(s: String): Option[URI] = {
    val uri = new URI(s)
    variantToNormalized.get(uri.toASCIIString).orElse {
      val httpsUri = new URI("https", uri.getHost, uri.getPath)
      variantToNormalized.get(httpsUri.toASCIIString)
    }.map(new URI(_))
  }
}
