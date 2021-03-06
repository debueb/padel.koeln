<%@include file="/WEB-INF/jsp/include/include.jsp"%>
<jsp:include page="/WEB-INF/jsp/include/head.jsp"/>

<div class="row">
    <div class="col-xs-12 col-sm-10 col-sm-offset-1 col-lg-8 col-lg-offset-2">
        <jsp:include page="/WEB-INF/jsp/include/back.jsp"/>

        <div class="page-header"></div>

        <div class="panel panel-info unit">
            <div class="panel-heading"><h4><fmt:message key="${empty Model.id ? 'NewMatchOffer' : 'EditMatchOffer'}"/></h4></div>
            <div class="panel-body">
                <c:if test="${empty Model.id}"><p>
                    <fmt:message key="NewMatchOfferDesc"/></p>
                </c:if>
                <spf:form method="POST" class="form-signin" role="form" modelAttribute="Model">
                    <spf:input type="hidden" path="id"/>
                    <div class="alert alert-danger" role="alert"><spf:errors path="*"/></div>

                    <%-- Datum --%>
                    <div class="datepicker-container">
                        <div class="datepicker-text-container form-top-element">
                            <div class="datepicker-label"><fmt:message key="Date"/></div>
                            <span class="fa fa-calendar datepicker-icon"></span>
                            <div class="datepicker-text"></div>
                        </div>
                        <spf:input type="hidden" path="startDate" class="datepicker-input form-control" value="${Model.startDate}" />
                        <div class="datepicker" data-show-on-init="false" data-allow-past="false"></div>
                    </div>

                    <%-- Start --%>
                    <span class="relative input-hour">
                        <spf:select path="startTimeHour" class="select-simple form-left-element form-center-element" data-container="body">
                            <c:forEach var="hour" begin="0" end="23">
                                <fmt:formatNumber value="${hour}" minIntegerDigits="2" var="hour"/>
                                <spf:option value="${hour}"/>
                            </c:forEach>
                        </spf:select>
                        <span class="explanation-select"><fmt:message key="Hour"/></span>
                    </span>
                    <div>
                        <span class="relative input-hour">
                            <spf:select path="startTimeMinute" class="select-simple form-right-element form-center-element" data-container="body">
                                <c:forEach var="minute" begin="0" end="30" step="30">
                                    <fmt:formatNumber value="${minute}" minIntegerDigits="2" var="minute"/>
                                    <spf:option value="${minute}"/>
                                </c:forEach>
                            </spf:select>
                            <span class="explanation-select"><fmt:message key="Minute"/></span>
                        </span>

                        <div class="clearfix"></div>
                    </div>

                    <%-- Dauer --%>
                    <div class="relative"> 
                        <spf:select path="duration" class="select-simple form-center-element form-control" data-container="body">
                            <spf:option value="60">60 <fmt:message key="Minutes"/></spf:option>
                            <spf:option value="90">90 <fmt:message key="Minutes"/></spf:option>
                            <spf:option value="120">120 <fmt:message key="Minutes"/></spf:option>
                            <spf:option value="150">150 <fmt:message key="Minutes"/></spf:option>
                        </spf:select>
                        <span class="explanation-select"><fmt:message key="Duration"/></span>
                    </div>

                  
                    <%-- Min. Teilnehmer --%>
                    <div class="input-group">
                        <span class="input-group-btn">
                            <button type="button" class="btn btn-default btn-plus-minus form-control form-center-element form-left-element" data-type="minus" data-field="minPlayersCount" data-container="body">
                                <span class="fa fa-minus"></span>
                            </button>
                        </span>
                        <span class="relative">
                            <spf:input type="text" path="minPlayersCount" class="form-control text-center input-plus-minus form-center-element" min="4" max="8"/>
                            <span class="explanation"><fmt:message key="MinPlayersCount"/></span>
                        </span>
                        <span class="input-group-btn">
                            <button type="button" class="btn btn-default btn-plus-minus form-control form-center-element form-right-element " data-type="plus" data-field="minPlayersCount" data-container="body">
                                <span class="fa fa-plus"></span>
                            </button>
                        </span>
                    </div>

                    <%-- Max. Teilnehmer --%>
                    <div class="input-group">
                        <span class="input-group-btn">
                            <button type="button" class="btn btn-default btn-plus-minus form-control form-center-element form-left-element" data-type="minus" data-field="maxPlayersCount" data-container="body">
                                <span class="fa fa-minus"></span>
                            </button>
                        </span>
                        <span class="relative">
                            <spf:input type="text" path="maxPlayersCount" class="form-control text-center input-plus-minus form-center-element" min="4" max="8"/>
                            <span class="explanation"><fmt:message key="MaxPlayersCount"/></span>
                        </span>
                        <span class="input-group-btn">
                            <button type="button" class="btn btn-default btn-plus-minus form-control form-center-element form-right-element" data-type="plus" data-field="maxPlayersCount" data-container="body">
                                <span class="fa fa-plus"></span>
                            </button>
                        </span>
                    </div>

                    <%-- Teilnehmer --%>
                    <div class="relative">
                        <fmt:message key="Participants" var="Participants"/>
                        <fmt:message key="CurrentlySelected" var="CurrentlySelected"/>
                        <fmt:message key="PleaseChoose" var="EmptyTitle"/>
                        <fmt:message key="ErrorText" var="ErrorText"/>
                        <fmt:message key="Search" var="SearchPlaceholder"/>
                        <fmt:message key="StatusInitialized" var="StatusInitialized"/>
                        <fmt:message key="SearchNoResults" var="SearchNoResults"/>
                        <fmt:message key="StatusSearching" var="StatusSearching"/>
                        <spf:select
                            path="players"
                            class="select-multiple show-tick form-control select-ajax-search"
                            data-style="form-center-element"
                            title="${Participants}"
                            multiple="true"
                            data-abs-ajax-url="/api/players/options"
                            data-live-search="true"
                            data-abs-locale-currently-selected='${CurrentlySelected}'
                            data-abs-locale-empty-title='${EmptyTitle}'
                            data-abs-locale-error-text='${ErrorText}'
                            data-abs-locale-search-placeholder='${SearchPlaceholder}'
                            data-abs-locale-status-initialized='${StatusInitialized}'
                            data-abs-locale-search-no-results='${SearchNoResults}'
                            data-abs-locale-status-searching='${StatusSearching}'
                            data-container="body">
                            <spf:options items="${Model.players}" itemValue="UUID"/>
                        </spf:select>
                        <span class="explanation-select"><fmt:message key="Participants"/></span>
                    </div>
                    
                    <%-- Spielst�rken --%>
                    <div class="relative">
                        <fmt:message key="SkillLevel" var="SkillLevel"/>
                        <spf:select path="skillLevels" class="select-multiple show-tick form-control" data-style="form-bottom-element" title="${SkillLevel}" multiple="multiple" data-container="body">
                            <c:forEach var="SkillLevel" items="${SkillLevels}">
                                <c:set var="selected" value="${fn:contains(Model.skillLevels, SkillLevel) ? 'selected': 'false'}"/>
                                <spf:option value="${SkillLevel}"><fmt:message key="${SkillLevel}"/></spf:option>
                            </c:forEach>
                        </spf:select>
                        <span class="explanation-select"><fmt:message key="SkillLevel"/></span>
                    </div>

                    <button class="btn btn-primary btn-block btn-form-submit unit" type="submit"><fmt:message key="Save"/></button>
                    <a class="btn btn-primary btn-block unit" href="/matchoffers"><fmt:message key="Cancel"/></a>
                    <c:if test="${not empty Model.id}">
                        <a class="btn btn-primary btn-block unit" href="/matchoffers/${Model.id}/delete"><fmt:message key="Delete"/></a>
                    </c:if>
                </spf:form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/include/footer.jsp"/>
