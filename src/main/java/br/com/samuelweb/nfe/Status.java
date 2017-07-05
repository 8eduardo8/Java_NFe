package br.com.samuelweb.nfe;

import br.com.samuelweb.nfe.exception.NfeException;
import br.com.samuelweb.nfe.exception.NfeValidacaoException;
import br.com.samuelweb.nfe.util.*;
import br.inf.portalfiscal.nfe.schema_4.consStatServ.TConsStatServ;
import br.inf.portalfiscal.nfe.schema_4.retConsStatServ.TRetConsStatServ;
import br.inf.portalfiscal.nfe_4.wsdl.NFeStatusServico4Stub;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.rmi.RemoteException;


/**
 * Classe responsavel por fazer a Verificacao do Status Do Webservice
 * 
 * @author Samuel Oliveira
 *
 */
public class Status {

	public static TRetConsStatServ statusServico(TConsStatServ consStatServ, boolean valida , String tipo) throws NfeException {

		ConfiguracoesIniciaisNfe configuracoesNfe = CertificadoUtil.iniciaConfiguracoes();
		boolean nfce = tipo.equals(ConstantesUtil.NFCE);

		try {

			String xml = XmlUtil.objectToXml(consStatServ);
	
			if(valida){
				String erros = Validar.validaXml(xml, Validar.STATUS);
				if(!ObjetoUtil.isEmpty(erros)){
					throw new NfeValidacaoException("Erro Na Validação do Xml: "+erros);
				}
			}
			
			System.out.println("Xml Status: "+xml);
			OMElement ome = AXIOMUtil.stringToOM(xml);

			NFeStatusServico4Stub.NfeDadosMsg dadosMsg = new NFeStatusServico4Stub.NfeDadosMsg();
			dadosMsg.setExtraElement(ome);

			NFeStatusServico4Stub stub = new NFeStatusServico4Stub(nfce ? WebServiceUtil.getUrl(ConstantesUtil.NFCE, ConstantesUtil.SERVICOS.STATUS_SERVICO) : WebServiceUtil.getUrl(ConstantesUtil.NFE, ConstantesUtil.SERVICOS.STATUS_SERVICO));
			NFeStatusServico4Stub.NfeResultMsg result = stub.nfeStatusServicoNF(dadosMsg);

			return XmlUtil.xmlToObject(result.getExtraElement().toString(), TRetConsStatServ.class);

		} catch (RemoteException | XMLStreamException | JAXBException e) {
			throw new NfeException(e.getMessage());
		}
		
	}
	
}