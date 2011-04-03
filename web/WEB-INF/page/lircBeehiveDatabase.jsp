
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../../common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" >
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Beehive Database</title>
    <link href="image/OpenRemote_Logo16x16.png" rel="shortcut icon"/>
    <link href="image/OpenRemote_Logo16x16.png" type="image/png" rel="icon"/>
    <link type="text/css" href="css/openremote_base.css" rel="stylesheet"/>
    <link type="text/css" href="css/beehiveDatabase.css" rel="stylesheet"/>
    <script type="text/javascript" src="jslib/jquery-1.3.1.min.js"></script>
    <script type="text/javascript" src="js/beehiveDatabase.js"></script>
</head>
<body>

<div id="main">
    <div id="title">
        <h1>Beehive Database</h1>
    </div>
    <div id="undecoratedLink">
        [
        <a class="regularLink" target="" href="changes.htm" title="Administration">
            Administration
        </a>
        ]
    </div>
    <div id="content">
        <div class="content_head">
            <h2>Browse LIRC database</h2>
        </div>
        <div id="content_body">
            <div id="lirc_select">
                <div id="vendor_select_container" class="lirc_select_container">
                    <div class="select_container_title">
                        <h3>Select device vendor:</h3>
                    </div>
                    <div class="select_container_head">
                        <input class="filter_input" type="text" value="Filter..." tabindex="1"/>
                        <button class="buttonNonpersistent resetBtn">Reset</button>
                    </div>
                    <div class="select_wrapper">
                        <select id="vendor_select" multiple="multiple" size="8"
                                onchange="showSelect(this.id,this.options[this.selectedIndex].value)">
                            <option value="0">------------</option>
                            <c:forEach items="${vendors}" var="vendor">
                                <option value="${vendor.id}" <c:if test="${vendor.name eq vendorName}">selected</c:if>>${vendor.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <div id="model_select_container" class="lirc_select_container" <c:if test="${vendorName eq null}">style="display:none"</c:if> <c:if test="${vendorName ne null}">style="display:block"</c:if>>
                    <div class="select_container_title">
                        <h3>Select device model:</h3>
                    </div>
                    <div class="select_container_head">
                        <input type="text" value="Filter..." class="filter_input" tabindex="1"/>
                        <button class="buttonNonpersistent resetBtn">Reset</button>
                    </div>
                    <div class="select_wrapper">
                    	<select id="model_select" multiple="multiple" size="8"
                                onchange="showSelect(this.id,this.options[this.selectedIndex].value)">
                            <option value="0">------------</option>
                            <c:forEach items="${models}" var="model">
                                <option value="${model.id}" <c:if test="${model.name eq modelName}">selected</c:if>>${model.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <div id="section_select_container" class="lirc_select_container" <c:if test="${showSection eq null}">style="display:none"</c:if> <c:if test="${showSection eq true}">style="display:block"</c:if>>
                    <div class="select_container_title">
                        <h3>Select device section:</h3>
                    </div>
                    <div class="select_container_head">
                        <input type="text" value="Filter..." class="filter_input" tabindex="1"/>
                        <button class="buttonNonpersistent resetBtn">Reset</button>
                    </div>
                    <div class="select_wrapper">
                    	<select id="remote_select" multiple="multiple" size="8"
                                onchange="showSelect(this.id,this.options[this.selectedIndex].value)">
                            <option value="0">------------</option>
                            <c:forEach items="${sections}" var="section">
                                <option value="${section.id}" <c:if test="${section.id eq sectionId}">selected</c:if>>${section.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>

            </div>
            <div class="clear"></div>
            <div id="lirc_details_container" <c:if test="${showDetail eq null}">style="display:none"</c:if> <c:if test="${showDetail eq true}">style="display:block"</c:if>>
            	<div>
				<button onclick="window.open('lirc.html?method=export&id=${model.id}')"
					class="buttonNonpersistent">Download LIRC configuration file</button>
				</div>
				<div id="lirc_details_title" class="content_head">
				    LIRC File Details
				</div>
				<div class="lirc_entry">
				    <div class="label">Selected LIRC file:</div>
				    <div class="output">${model.fileName}</div>
				</div>
				<div class="lirc_entry">
				    <div class="label">Model:</div>
				    <div class="output">${model.name}</div>
				</div>
				<div class="lirc_entry">
				    <div class="label">Supported devices:</div>
				    <div class="output">UNKNOWN</div>
				</div>
				<div id="lirc_comment" class="more_detail_container">
				    <div class="detail_title">
				        Comments
				    </div>
				    <div class="detail_content">
				        <pre>${section.comment}</pre>
				    </div>
				</div>
				
				<div class="lirc_entry">
				    <div class="label">Selected remote:</div>
				    <div class="output">${options[0].value}</div>
				</div>
				<div class="lirc_entry">
				    <div class="label">Flags:</div>
				    <div class="output">${options[2].value}</div>
				</div>
				<div id="lirc_code_option" class="more_detail_container">
				    <div class="detail_title">
				        Infrared Code Options
				    </div>
				    <div class="detail_content">
				        <table class="data_table">
				            <tbody>
				            <c:forEach items="${options}" var="option" varStatus="n">
				                <c:if test="${!option.blankComment}">
				                    <tr class="comment_row">
				                        <td colspan="2">
				                            <pre>${option.comment}</pre>
				                        </td>
				                    </tr>
				                </c:if>
				                <tr>
				                    <td class="twentyPercentColumn">${option.name}</td>
				                    <td class="defaultColumn">${option.value}</td>
				                </tr>
				            </c:forEach>
				            </tbody>
				        </table>
				    </div>
				</div>
				<br/>
				
				<div id="lirc_codes" class="more_detail_container">
				    <div class="detail_title">
				        Infrared Codes
				    </div>
				    <div class="detail_content">
				        <table class="data_table">
				            <tbody>
				            <c:forEach items="${codes}" var="code">
				                  <c:if test="${!code.blankComment}">
				                    <tr class="comment_row">
				                        <td colspan="2">
				                            <pre>${code.comment}</pre>
				                        </td>
				                    </tr>
				                </c:if>
				                <tr>
				                    <td class="twentyPercentColumn">${code.name}</td>
				                    <td class="defaultColumn wrapWhitespace">
				                        <pre>${code.value}</pre>
				                    </td>
				                </tr>
				            </c:forEach>
				            </tbody>
				        </table>
				    </div>
				</div>
				
				<div id="bottom">
				    <button onclick="window.open('lirc.html?method=export&id=${model.id}')" class="buttonNonpersistent">Download LIRC configuration file
				    </button>
				</div>
				<c:if test="${showDetail eq true}">
					<script>
						bindDetailTitleEvent();
				    	fillTableInEven();
					</script>
				</c:if>
            </div>
        </div>
    </div>

</div>

<div id="waiting_div" style="display: none;">
	<img src="image/statusindicator.gif" alt="wating" />
</div>
</body>
</html>

