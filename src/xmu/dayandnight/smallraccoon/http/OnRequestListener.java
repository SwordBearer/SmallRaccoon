package xmu.dayandnight.smallraccoon.http;

/**
 * @author SwordBearer  e-mail :ranxiedao@163.com
 *         Created by SwordBearer on 13-8-12.
 */
public interface OnRequestListener {
    public void onError(Object obj);

    public void onFinished(Object obj);

    public void onRunning(Object ojb);
}
