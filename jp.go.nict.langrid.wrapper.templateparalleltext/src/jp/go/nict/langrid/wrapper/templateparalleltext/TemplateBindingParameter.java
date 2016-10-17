package jp.go.nict.langrid.wrapper.templateparalleltext;

import static jp.go.nict.langrid.wrapper.templateparalleltext.db.GetTemplatesDAO.REGEX_PART_CHOICES;
import static jp.go.nict.langrid.wrapper.templateparalleltext.db.GetTemplatesDAO.REGEX_PART_VALUE;

import java.security.InvalidParameterException;
import java.text.MessageFormat;

import jp.go.nict.langrid.service_1_2.templateparalleltext.BoundChoiceParameter;
import jp.go.nict.langrid.service_1_2.templateparalleltext.BoundValueParameter;
import jp.go.nict.langrid.service_1_2.templateparalleltext.Choice;
import jp.go.nict.langrid.service_1_2.templateparalleltext.ChoiceParameter;
import jp.go.nict.langrid.service_1_2.templateparalleltext.Template;
import jp.go.nict.langrid.service_1_2.templateparalleltext.ValueParameter;

public class TemplateBindingParameter {

	private Template template;

	private BoundChoiceParameter[] boundChoiceParameters;

	private BoundValueParameter[] boundValueParameters;

	private static final String CHICES_PARAM_REGEX = "<param id=\"{0}\"\\s+"
			+ REGEX_PART_CHOICES + "\\s*/>";

	private static final String VALUE_PARAM_REGEX = "<param id=\"{0}\"\\s+"
			+ REGEX_PART_VALUE + "\\s*/>";

	public TemplateBindingParameter(Template template) {
		this.template = template;
	}

	public TemplateBindingParameter(Template template,
			BoundChoiceParameter[] cParam, BoundValueParameter[] rParam) {
		this(template);
		this.boundChoiceParameters = cParam;
		this.boundValueParameters = rParam;
	}

	public String toStringBoundParameter() {
		String templateStr = template.getTemplate();

		if (boundChoiceParameters != null) {
			templateStr = bindChoiceParamters(templateStr);
		}

		if (boundValueParameters != null) {
			templateStr = bindValueParamters(templateStr);
		}

		return templateStr;
	}

	protected String bindChoiceParamters(String templateStr) {

		for (BoundChoiceParameter boundParam : boundChoiceParameters) {
			String paramId = boundParam.getParameterId();

			ChoiceParameter cParam = getChoiceParamtersByParameterId(
					template.getChoiceParameters(), paramId);

			if (cParam != null) {
				Choice choice = getChoiceByChoiceId(cParam.getChoices(),
						boundParam.getChoiceId());

				if (choice != null) {
					String regex = MessageFormat.format(CHICES_PARAM_REGEX,
							paramId);

					templateStr = templateStr.replaceAll(regex,
							choice.getValue());
				}
			}
		}

		return templateStr;
	}

	protected String bindValueParamters(String templateStr) {

		for (BoundValueParameter bParam : boundValueParameters) {
			String paramId = bParam.getParameterId();
			
			ValueParameter param = getValueParamtersByRangeId(
					template.getValueParameters(), bParam.getParameterId());

			if (param != null) {
				String regex = MessageFormat.format(VALUE_PARAM_REGEX,
						paramId);
				
				templateStr = templateStr.replaceAll(regex,
						String.valueOf(bParam.getValue()));
			}
		}

		return templateStr;
	}

	public void setBoundChoiceParameter(
			BoundChoiceParameter[] boundChoiceParameter) {
		this.boundChoiceParameters = boundChoiceParameter;
	}

	public void setBoundValueParameter(BoundValueParameter[] boundValueParameter) {
		this.boundValueParameters = boundValueParameter;
	}

	protected ChoiceParameter getChoiceParamtersByParameterId(
			ChoiceParameter[] choiceParameters, String parameterId) {
		for (ChoiceParameter cParam : choiceParameters) {
			if (cParam.getParameterId().equals(parameterId)) {
				return cParam;
			}
		}
		return null;
	}

	protected Choice getChoiceByChoiceId(Choice[] choices, String choiceId) {
		for (Choice choice : choices) {
			if (choice.getChoiceId().equals(choiceId)) {
				return choice;
			}
		}
		return null;
	}

	protected ValueParameter getValueParamtersByRangeId(
			ValueParameter[] valueParameters, String paramId) {
		for (ValueParameter param : valueParameters) {
			if (param.getParameterId().equals(paramId)) {
				return param;
			}
		}
		return null;
	}

	protected void validate() {

		validateBoundChoiceParamters();

		validateBoundValueParamters();

		if (boundChoiceParameters != null) {
			for (BoundChoiceParameter bcp : boundChoiceParameters) {
				validateBoundChoiceParamter(bcp);
			}
		}

		if (boundValueParameters != null) {
			for (BoundValueParameter brp : boundValueParameters) {
				validateBoundValueParamter(brp);
			}
		}
	}

	/*
	 * 定義されたChoiceパラメータが全て入力値として与えられているか？
	 */
	protected void validateBoundChoiceParamters() {
		for (ChoiceParameter cParam : template.getChoiceParameters()) {
			boolean exist = false;
			for (BoundChoiceParameter bcp : this.boundChoiceParameters) {
				if (bcp.getParameterId().equals(cParam.getParameterId())) {
					exist = true;
				}
			}

			if (!exist)
				throw new InvalidParameterException(
						"not found input choice parameter id: "
								+ cParam.getParameterId());
		}
	}

	/*
	 * 定義されたValueパラメータが全て入力値として与えられているか？
	 */
	protected void validateBoundValueParamters() {
		for (ValueParameter rParam : template.getValueParameters()) {
			boolean exist = false;
			for (BoundValueParameter brp : this.boundValueParameters) {
				if (brp.getParameterId().equals(rParam.getParameterId())) {
					exist = true;
				}
			}

			if (!exist)
				throw new InvalidParameterException(
						"not found input value parameter id: "
								+ rParam.getParameterId());
		}
	}

	/*
	 * 与えられたChoiceパラメータが正しいか？
	 */
	protected void validateBoundChoiceParamter(final BoundChoiceParameter bcp) {
		ChoiceParameter targetParam = null;
		for (ChoiceParameter cParam : template.getChoiceParameters()) {
			if (cParam.getParameterId().equals(bcp.getParameterId())) {
				targetParam = cParam;
			}
		}

		if (targetParam == null) {
			throw new InvalidParameterException(
					"undfined Choice Parameter id: " + bcp.getParameterId());
		}
		
		Choice targetChoice = null;
		for(Choice choice: targetParam.getChoices()) {
			if(choice.getChoiceId().equals(bcp.getChoiceId())) {
				targetChoice = choice;
			}
		}
		
		if(targetChoice == null) throw new InvalidParameterException("undefined Choice id: " + bcp.getChoiceId());
	}

	/*
	 * 与えられたValueパラメータが正しいか？
	 */
	protected void validateBoundValueParamter(BoundValueParameter brp) {
		ValueParameter targetParam = null;
		for (ValueParameter rParam : template.getValueParameters()) {
			if (rParam.getParameterId().equals(brp.getParameterId())) {
				targetParam = rParam;
			}
		}

		if (targetParam == null) {
			throw new InvalidParameterException("undfined Value Parameter id: "
					+ brp.getParameterId());
		}
/*
		if (!isInRange(targetParam.getLowerBound(),
				targetParam.getUpperBound(), brp.getValue())) {
			throw new InvalidParameterException("out of range value id: "
					+ brp.getParameterId() + " value:" + brp.getValue());
		}
*/
	}

	protected boolean isInRange(int start, int end, int value) {
		return start <= value && value <= end;
	}
}
