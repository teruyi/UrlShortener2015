package urlshortener.bangladeshgreen.colaEjemplo;

import org.springframework.stereotype.Component;

/**
 * Worker que realiza una tarea a partir de la colaEjemplo.
 * Es lanzado por el listener.
 * ES EL THREAD QUE REALIZA LA TAREA "GORDA", COMO COMPROBAR SI ESTA ACTIVA, MANDAR UN MAIL,...
 */
@Component
public class WorkerEjemplo implements Runnable {

    private String param;
    public void setParameter(String param){
        this.param = param;

    }

    @Override
    public void run() {
        long id =  Thread.currentThread().getId();
        System.out.println("Worker - " + param + " - ID:" + id);
    }
}
