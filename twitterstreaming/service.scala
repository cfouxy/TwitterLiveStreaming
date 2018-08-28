package com.socgen.drhg.dhr.api.services

import com.google.inject.{ImplementedBy, Inject}
import com.mongodb.casbah.Imports.MongoDBObject
import com.mongodb.casbah.MongoClient
import com.mongodb.{DBCursor, DBObject}
import com.socgen.drhg.dhr.api.models.{EnrichmentByEntity, EnrichmentField}
import com.twitter.inject.Logging

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

@ImplementedBy(classOf[MongoDbEnrichmentService])
trait EnrichmentService extends Logging {
  def fetchEnrichmentInfo: Array[EnrichmentByEntity]
}

class MongoDbEnrichmentService @Inject()(mongoClient: MongoClient) extends EnrichmentService {

  override def fetchEnrichmentInfo: Array[EnrichmentByEntity] = {
    Try(mongoClient("dhr")
      .getCollection("bvDictionary")
      .find(MongoDBObject(), MongoDBObject("entity" -> 1, "domain" -> 1, "fields.name" -> 1, "fields.enrichment" -> 1, "_id" -> 0))) match {
      case Success(cursor) =>
        val enrichmentByDomainAndEntity = buildEnrichmentByDomainAndEntity(cursor)
        cursor.close()
        enrichmentByDomainAndEntity
      case Failure(_) => throw new RuntimeException("Enrichment info not found")
    }
  }

  private def buildEnrichmentByDomainAndEntity(cursor: DBCursor): Array[EnrichmentByEntity] = {
    Try(cursor.toArray.asScala.filter(obj => obj.containsField("entity"))) match {
      case Success(parent) =>
        parent.flatMap(root => {
          buildEnrichmentByField(root.get("entity").toString, root) match {
            case fields if fields.nonEmpty => Some(EnrichmentByEntity(root.get("domain").toString, root.get("entity").toString, fields))
            case _ => None
          }
        }).toArray
      case Failure(_) => logger.error("Enrichment info not found"); println("je suis ici !!!!"); null
    }
  }

  private def buildEnrichmentByField(entityName: String, root: DBObject): Seq[EnrichmentField] = {
    root.get("fields").asInstanceOf[DBObject].toMap.asScala.flatMap(field => {
      val fieldDBObject = field._2.asInstanceOf[DBObject].get("enrichment").asInstanceOf[DBObject]
      if (fieldDBObject != null) Some(EnrichmentField(field._2.asInstanceOf[DBObject].get("name").toString, fieldDBObject))
      else None

    }).toSeq
  }
}
