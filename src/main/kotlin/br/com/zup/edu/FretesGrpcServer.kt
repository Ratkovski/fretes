package br.com.zup.edu

import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory

import javax.inject.Singleton
import kotlin.random.Random


@Singleton
class FretesGrpcServer : FretesServiceGrpc.FretesServiceImplBase() {

    private val logger = LoggerFactory.getLogger(FretesGrpcServer::class.java)


    override fun calculaFrete(request: CalculaFreteRequest?, responseObserver: StreamObserver<CalculaFreteResponse>?) {
       logger.info("Calculando o frete para a requesr: $request")


        val response = CalculaFreteResponse.newBuilder()
            .setCep(request!!.cep)
            .setValor(Random.nextDouble(from = 0.0,until = 140.0))
            .build()

        logger.info("frete calculado $response")

        responseObserver!!.onNext(response)
        responseObserver.onCompleted()
    }
}