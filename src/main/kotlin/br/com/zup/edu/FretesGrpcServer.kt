package br.com.zup.edu

import com.google.protobuf.Any
import com.google.rpc.Code

import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import io.grpc.protobuf.StatusProto.toStatusRuntimeException
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import java.lang.IllegalArgumentException

import javax.inject.Singleton
import kotlin.random.Random


@Singleton
class FretesGrpcServer : FretesServiceGrpc.FretesServiceImplBase() {

    private val logger = LoggerFactory.getLogger(FretesGrpcServer::class.java)


    override fun calculaFrete(request: CalculaFreteRequest?, responseObserver: StreamObserver<CalculaFreteResponse>?) {
       logger.info("Calculando o frete para a requesr: $request")

        val cep= request?.cep
        if (cep == null || cep.isBlank()){
        val e = Status.INVALID_ARGUMENT
            .withDescription("cep deve ser informado ")
            .asRuntimeException()
            responseObserver?.onError(e)
        }


        if (!cep!!.matches("[0-9]{5}-[0-9]{3}".toRegex())){
            val e = Status.INVALID_ARGUMENT
                .withDescription("cep invalido")
                .augmentDescription(" o formato esperado deve ser 99999-999")
                .asRuntimeException()
            responseObserver?.onError(e)

        }


        //SIMULAR uma verificaçãpo de segurança
        if (cep.endsWith("333")){
          val statusProto = com.google.rpc.Status.newBuilder()
                .setCode(Code.PERMISSION_DENIED.number)
                .setMessage("usuario não pode acessar este recurso")
                .addDetails(Any.pack(ErroDetails.newBuilder()
                  .setCode(401)
                  .setMessage("token expirado").build()))
                .build()
          val e = StatusProto.toStatusRuntimeException(statusProto)
            responseObserver?.onError(e)
        }


        var valor = 0.0
        try {
        valor = Random.nextDouble(from = 0.0,until = 140.0)//logica complexa

            if (valor>100){
                throw IllegalStateException("Erro inesperado ao executar logica de negócio")
            }

        }catch (e:Exception){
            responseObserver?.onError(Status.INTERNAL
                .withDescription(e.message)
                .withCause(e)//E anexado ao status de erro, mas não é enviado ao cliente
                .asRuntimeException())


        }



        val response = CalculaFreteResponse.newBuilder()
            .setCep(request!!.cep)
            .setValor(valor)
      //      .setValor(Random.nextDouble(from = 0.0,until = 140.0))//logica complexa
            .build()

        logger.info("frete calculado $response")

        responseObserver!!.onNext(response)
        responseObserver.onCompleted()
    }
}