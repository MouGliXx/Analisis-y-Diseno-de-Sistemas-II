package servidor;

public class Sincronizacion implements ISincronizacion{
        private int puertoRedundancia;
        private boolean hayRedundancia = false;

        public Sincronizacion(int puertoRedundancia, boolean hayRedundancia) {
                this.puertoRedundancia = puertoRedundancia;
                this.hayRedundancia = hayRedundancia;
        }

        @Override
        public void sincronizacion(){
                System.out.printf("Se sincronizo");
        };

        @Override
        public void conectarRedundancia() {
                System.out.printf("Nos conectamos a la redundancia");
        }

        public int getPuertoRedundancia() {
                return puertoRedundancia;
        }

        public void setPuertoRedundancia(int puertoRedundancia) {
                this.puertoRedundancia = puertoRedundancia;
        }

        public boolean isHayRedundancia() {
                return hayRedundancia;
        }

        public void setHayRedundancia(boolean hayRedundancia) {
                this.hayRedundancia = hayRedundancia;
        }
}
