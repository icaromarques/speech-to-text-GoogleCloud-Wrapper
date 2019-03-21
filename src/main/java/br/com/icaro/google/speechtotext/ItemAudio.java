package br.com.icaro.google.speechtotext;

/**
 * @author icaroafonso
 * Classe que contém as informações das palavras no audio reconhecido através do speechToText
 */
public class ItemAudio {

	private String palavra;
	private String inicio;
	private String fim;
	
	public String getPalavra() {
		return palavra;
	}
	public void setPalavra(String palavra) {
		this.palavra = palavra;
	}
	public String getInicio() {
		return inicio;
	}
	public void setInicio(String inicio) {
		this.inicio = inicio;
	}
	public String getFim() {
		return fim;
	}
	public void setFim(String fim) {
		this.fim = fim;
	}
	@Override
	public String toString() {
		return "ItemAudio [palavra=" + palavra + ", inicio=" + inicio + "secs , fim=" + fim + "secs ]";
	}
	
	
	
	
}
