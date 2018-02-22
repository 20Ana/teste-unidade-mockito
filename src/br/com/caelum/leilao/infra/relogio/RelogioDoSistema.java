package br.com.caelum.leilao.infra.relogio;

import com.sun.org.apache.regexp.internal.RE;

import java.util.Calendar;

/**
 * Created by ana on 21/02/18.
 */
public class RelogioDoSistema implements Relogio{

    public Calendar hoje() {
        return Calendar.getInstance();
    }
}
