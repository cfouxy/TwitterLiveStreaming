package com.socgen.drhg.dhr.api


import java.time.{LocalDate, LocalDateTime}

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.databind.{ObjectMapper, PropertyNamingStrategy}
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.{LocalDateDeserializer, LocalDateTimeDeserializer}
import com.fasterxml.jackson.datatype.jsr310.ser.{LocalDateSerializer, LocalDateTimeSerializer}
import com.github.racc.tscg.TypesafeConfigModule
import com.github.xiaodongw.swagger.finatra.SwaggerController
import com.google.inject.{Module, Provides, Singleton}
import com.mongodb.casbah.{MongoClient, MongoClientURI}
import com.socgen.drhg.dhr.api.auth.controllers.AuthController
import com.socgen.drhg.dhr.api.auth.filters.AuthenticatingFilter
import com.socgen.drhg.dhr.api.controllers._
import com.socgen.drhg.dhr.api.jdbc.PostgresJdbcClient
import com.socgen.drhg.dhr.api.models.PostgresConfiguration
import com.socgen.drhg.dhr.api.utils.DateTimeFormatterUtil._
import com.twitter.app.Flag
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.finatra.json.modules.FinatraJacksonModule
import com.twitter.inject.{Logging, TwitterModule}
import com.typesafe.config.ConfigFactory
import io.swagger.models.{Info, Swagger}


object DhrApiJacksonModule extends FinatraJacksonModule {

	override val propertyNamingStrategy = PropertyNamingStrategy.LOWER_CAMEL_CASE
	override val serializationInclusion: JsonInclude.Include = Include.ALWAYS

	val javaTimeModule = new JavaTimeModule
	javaTimeModule.addSerializer(classOf[LocalDate], new LocalDateSerializer(dateFormatter))
	javaTimeModule.addDeserializer(classOf[LocalDate], new LocalDateDeserializer(dateFormatter))

	javaTimeModule.addSerializer(classOf[LocalDateTime], new LocalDateTimeSerializer(dateTimeFormatter))
	javaTimeModule.addDeserializer(classOf[LocalDateTime], new LocalDateTimeDeserializer(dateTimeFormatter))

	override val additionalJacksonModules = Seq(
		new JodaModule(),
		javaTimeModule
	)

	override def additionalMapperConfiguration(mapper: ObjectMapper) {
		//    mapper.configure(Feature.WRITE_NUMBERS_AS_STRINGS, true)
	}

}

object DhrMongoDbModule extends TwitterModule {

	val mongoDbUri: Flag[String] = flag(name = "mongoDB.uri", default = "mongodb://localhost:27017", help = "The MongoDB client URI")

	@Singleton
	@Provides
	def providesMongoClient: MongoClient = {
		logger.info("Creating MongoDB connection with following parameters:")
		logger.info(s" - mongoDB: '${mongoDbUri()}'")
		MongoClient(MongoClientURI(mongoDbUri()))
	}
}

object LoggerModule extends TwitterModule {

//  val elasticUri: Flag[String] = flag(name = "elastic.uri", default = "localhost:9200", help = "The Elastic URI")
	//val elasticUri: Flag[String] = flag(name = "elastic.uri", default = "localhost:9200", help = "The Elastic URI")

  @Singleton
  @Provides
  def providesLogger: CommonLogger = {
//    new ElasticLogger(elasticUri())
		ConsolLogger
  }
}

object DhrPostgresModule extends TwitterModule {

	val postgresHost: Flag[String] = flag(name = "postgres.host", default = "localhost", help = "The Postgres host")
	val postgresPort: Flag[Int] = flag(name = "postgres.port", default = 5432, help = "The Postgres port")
	val postgresUsername: Flag[String] = flag(name = "postgres.username", default = "", help = "The Postgres username")
	val postgresPassword: Flag[String] = flag(name = "postgres.password", default = "", help = "The Postgres password")
	val postgresDatabase: Flag[String] = flag(name = "postgres.database", default = "", help = "The Postgres database")
	val postgresSchema: Flag[String] = flag(name = "postgres.schema", default = "", help = "The Postgres schema")

	@Singleton
	@Provides
	def providesPostgresClient: PostgresJdbcClient = {
		logger.info("Creating PostgreSQL connection pool with following parameters:")
		logger.info(s" - user: '${postgresUsername()}'")
		logger.info(s" - password: '****************'")
		logger.info(s" - Host: '${postgresHost()}'")
		logger.info(s" - Port: '${postgresPort()}'")
		logger.info(s" - Database: '${postgresDatabase()}'")

		PostgresJdbcClient("org.postgresql.Driver",s"jdbc:postgresql://${postgresHost()}:${postgresPort()}/${postgresDatabase()}",postgresUsername(), postgresPassword())
	}

	@Singleton
	@Provides
	def providesPostgresConfiguration: PostgresConfiguration = {
		PostgresConfiguration(schema = postgresSchema())
	}

}

object ConfigModule extends TwitterModule with Logging {
	override def configure() = {
		val config = ConfigFactory.load()
		install(TypesafeConfigModule.fromConfig(config))
	}
}

object DataHubSwagger extends Swagger

object DataHubApp extends DataHubServer

class DataHubServer extends HttpServer {

	override val modules: Seq[Module] = super.modules :+ DhrMongoDbModule :+ DhrPostgresModule :+ ConfigModule :+ LoggerModule
	val info: Info = new Info()
		.description("The DataHub API, this is a API for RH")
		.version("1.0.6")
		.title("DataHub API")

	override def jacksonModule: DhrApiJacksonModule.type = DhrApiJacksonModule

	override def configureHttp(router: HttpRouter) {
		router
			.filter[LoggingMDCFilter[Request, Response]]
			.filter[TraceIdMDCFilter[Request, Response]]
			.filter[CommonFilters]
			.filter[AuthenticatingFilter]
			.add[SplitFileController]
			.add[FunctionalErrorController]
			.add[SourceControlController]
			.add[SourceMappingController]
			.add[DraftControlController]
			.add[BvHistoryStrategyController]
			.add[EnrichmentController]
			.add[VersionController]
			//.add[EntityController]
			.add[AuthController]
  		.add[IdentityDataController]
			.add(new SwaggerController(swagger = DataHubSwagger))
	}

	DataHubSwagger.info(info)

	override protected def defaultFinatraHttpPort: String = ":8889"

}
