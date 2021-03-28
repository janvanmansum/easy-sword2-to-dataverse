/**
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
package nl.knaw.dans.easy.dd2d

import better.files.File
import nl.knaw.dans.easy.dd2d.dansbag.DansBagValidator
import nl.knaw.dans.easy.dd2d.migrationinfo.MigrationInfo
import nl.knaw.dans.lib.dataverse.DataverseInstance
import nl.knaw.dans.lib.taskqueue.PassiveTaskQueue

import java.nio.file.Path
import scala.util.Try
import scala.xml.Elem

class SingleDepositProcessor(deposit: File,
                             activeMetadataBlocks: List[String],
                             dansBagValidator: DansBagValidator,
                             dataverse: DataverseInstance,
                             migrationInfo: Option[MigrationInfo],
                             autoPublish: Boolean = true,
                             publishAwaitUnlockMaxNumberOfRetries: Int,
                             publishAwaitUnlockMillisecondsBetweenRetries: Int,
                             narcisClassification: Elem,
                             isoToDataverseLanage: Map[String, String],
                             reportIdToTerm: Map[String, String],
                             outboxDir: File) {
  def process(): Try[Unit] = Try {
    val ingestTasks = new PassiveTaskQueue[Deposit]()
    ingestTasks.add(
      DepositIngestTask(
        Deposit(deposit),
        activeMetadataBlocks,
        dansBagValidator,
        dataverse,
        migrationInfo,
        publish = autoPublish,
        publishAwaitUnlockMaxNumberOfRetries,
        publishAwaitUnlockMillisecondsBetweenRetries,
        narcisClassification,
        isoToDataverseLanage,
        reportIdToTerm,
        outboxDir))
    ingestTasks.process()
  }
}
